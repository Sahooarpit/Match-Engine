package com.example.matchengine;

import com.example.matchengine.repository.OrderRepository;
import com.example.matchengine.repository.TradeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Math.min;

@Service
@RequiredArgsConstructor
public class MatchEngine {

    // In-memory store for live order books for performance
    private final Map<Ticker, OrderBook> orderBooks = new ConcurrentHashMap<>();

    private final OrderRepository orderRepository;
    private final TradeRepository tradeRepository;
    private final ClientService clientService;

    /**
     * This method is automatically called by Spring after the application starts.
     * It loads all open orders from the database to reconstruct the in-memory order books.
     */
    @PostConstruct
    public void loadOrderBooksOnStartup() {
        System.out.println("Loading order books from database...");
        List<OrderStatus> openStatuses = Arrays.asList(OrderStatus.OPEN, OrderStatus.PARTIALLY_FILLED);
        List<Order> openOrders = orderRepository.findByStatusIn(openStatuses);

        for (Order order : openOrders) {
            OrderBook book = orderBooks.computeIfAbsent(order.getTicker(), ticker -> new OrderBook(ticker));
            if (order.getSide() == Side.BUY) {
                book.getBids().computeIfAbsent(order.getPrice(), p -> new ArrayDeque<>()).add(order);
            } else {
                book.getAsks().computeIfAbsent(order.getPrice(), p -> new ArrayDeque<>()).add(order);
            }
        }
        System.out.println("Finished loading " + openOrders.size() + " open orders.");
    }

    @Transactional
    public List<Trade> processOrder(Order newOrder) {
        // 1. Validate the order against the client's current holdings
        clientService.validateOrder(newOrder);

        // 2. Get the appropriate in-memory order book
        OrderBook book = orderBooks.computeIfAbsent(newOrder.getTicker(), ticker -> new OrderBook(ticker));

        // 3. Match the order and get executed trades and modified book orders
        List<Trade> executedTrades = new ArrayList<>();
        List<Order> modifiedBookOrders = new ArrayList<>();

        if (newOrder.getSide() == Side.BUY) {
            matchBuyOrder(newOrder, book, executedTrades, modifiedBookOrders);
        } else {
            matchSellOrder(newOrder, book, executedTrades, modifiedBookOrders);
        }

        // 4. If the new order is not fully filled, add it to the in-memory book
        if (newOrder.getRemainingQuantity() > 0) {
            addOrderToBook(newOrder, book);
        }

        // 5. Persist all changes to the database
        saveChanges(newOrder, modifiedBookOrders, executedTrades);

        return executedTrades;
    }

    private void matchBuyOrder(Order buyOrder, OrderBook book, List<Trade> executedTrades, List<Order> modifiedBookOrders) {
        // Iterate through asks (sell orders) from low price to high
        for (Map.Entry<BigDecimal, Deque<Order>> entry : book.getAsks().entrySet()) {
            if (buyOrder.getRemainingQuantity() == 0 || buyOrder.getPrice().compareTo(entry.getKey()) < 0) break;

            Iterator<Order> iterator = entry.getValue().iterator();
            while (iterator.hasNext()) {
                if (buyOrder.getRemainingQuantity() == 0) break;
                Order sellOrder = iterator.next();
                long matchQuantity = min(buyOrder.getRemainingQuantity(), sellOrder.getRemainingQuantity());

                Trade trade = new Trade(buyOrder.getOrderId(), sellOrder.getOrderId(), buyOrder.getClientId(), sellOrder.getClientId(), buyOrder.getTicker(), matchQuantity, sellOrder.getPrice());
                executedTrades.add(trade);

                buyOrder.setRemainingQuantity(buyOrder.getRemainingQuantity() - matchQuantity);
                sellOrder.setRemainingQuantity(sellOrder.getRemainingQuantity() - matchQuantity);
                modifiedBookOrders.add(sellOrder);

                if (sellOrder.getRemainingQuantity() == 0) {
                    iterator.remove(); // Remove filled order from the live book
                }
            }
        }
    }

    private void matchSellOrder(Order sellOrder, OrderBook book, List<Trade> executedTrades, List<Order> modifiedBookOrders) {
        // Iterate through bids (buy orders) from high price to low
        for (Map.Entry<BigDecimal, Deque<Order>> entry : book.getBids().entrySet()) {
            if (sellOrder.getRemainingQuantity() == 0 || sellOrder.getPrice().compareTo(entry.getKey()) > 0) break;

            Iterator<Order> iterator = entry.getValue().iterator();
            while (iterator.hasNext()) {
                if (sellOrder.getRemainingQuantity() == 0) break;
                Order buyOrder = iterator.next();
                long matchQuantity = min(sellOrder.getRemainingQuantity(), buyOrder.getRemainingQuantity());

                Trade trade = new Trade(buyOrder.getOrderId(), sellOrder.getOrderId(), buyOrder.getClientId(), sellOrder.getClientId(), sellOrder.getTicker(), matchQuantity, buyOrder.getPrice());
                executedTrades.add(trade);

                sellOrder.setRemainingQuantity(sellOrder.getRemainingQuantity() - matchQuantity);
                buyOrder.setRemainingQuantity(buyOrder.getRemainingQuantity() - matchQuantity);
                modifiedBookOrders.add(buyOrder);

                if (buyOrder.getRemainingQuantity() == 0) {
                    iterator.remove(); // Remove filled order from the live book
                }
            }
        }
    }
    
    private void addOrderToBook(Order order, OrderBook book) {
        if (order.getSide() == Side.BUY) {
            book.getBids().computeIfAbsent(order.getPrice(), p -> new ArrayDeque<>()).add(order);
        } else {
            book.getAsks().computeIfAbsent(order.getPrice(), p -> new ArrayDeque<>()).add(order);
        }
    }

    private void saveChanges(Order newOrder, List<Order> modifiedBookOrders, List<Trade> executedTrades) {
        // 1. Collect all orders whose state has changed
        List<Order> allModifiedOrders = new ArrayList<>(modifiedBookOrders);
        allModifiedOrders.add(newOrder);

        // 2. Update the status of all modified orders
        for (Order order : allModifiedOrders) {
            if (order.getRemainingQuantity() == 0) {
                order.setStatus(OrderStatus.FILLED);
            } else if (order.getRemainingQuantity() < order.getOriginalQuantity()) {
                order.setStatus(OrderStatus.PARTIALLY_FILLED);
            }
        }

        // 3. Save all changed orders and new trades
        orderRepository.saveAll(allModifiedOrders);
        if (!executedTrades.isEmpty()) {
            tradeRepository.saveAll(executedTrades);
            // 4. Update client holdings based on executed trades
            for (Trade trade : executedTrades) {
                clientService.updateHolding(trade.getSellClientId(), trade.getTicker(), BigDecimal.valueOf(trade.getQuantity()).negate());
                clientService.updateHolding(trade.getSellClientId(), Ticker.USDT, trade.getPrice().multiply(BigDecimal.valueOf(trade.getQuantity())));
                clientService.updateHolding(trade.getBuyClientId(), trade.getTicker(), BigDecimal.valueOf(trade.getQuantity()));
                clientService.updateHolding(trade.getBuyClientId(), Ticker.USDT, trade.getPrice().multiply(BigDecimal.valueOf(trade.getQuantity())).negate());
            }
        }
    }
}
