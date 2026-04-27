package com.example.matchengine;

import com.example.matchengine.repository.InstrumentRepository;
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

    private final Map<String, OrderBook> orderBooks = new ConcurrentHashMap<>(); // Key is now String

    private final OrderRepository orderRepository;
    private final TradeRepository tradeRepository;
    private final ClientService clientService;
    private final InstrumentRepository instrumentRepository;

    @PostConstruct
    public void loadOrderBooksOnStartup() {
        System.out.println("Loading order books from database...");
        // Initialize order books for all known instruments
        instrumentRepository.findAll().forEach(instrument -> {
            orderBooks.put(instrument.getTicker(), new OrderBook(instrument.getTicker()));
        });

        List<OrderStatus> openStatuses = Arrays.asList(OrderStatus.OPEN, OrderStatus.PARTIALLY_FILLED);
        List<Order> openOrders = orderRepository.findByStatusIn(openStatuses);

        for (Order order : openOrders) {
            OrderBook book = orderBooks.get(order.getTicker());
            if (book != null) { // Only add orders for known instruments
                if (order.getSide() == Side.BUY) {
                    book.getBids().computeIfAbsent(order.getPrice(), p -> new ArrayDeque<>()).add(order);
                } else {
                    book.getAsks().computeIfAbsent(order.getPrice(), p -> new ArrayDeque<>()).add(order);
                }
            }
        }
        System.out.println("Finished loading " + openOrders.size() + " open orders across " + orderBooks.size() + " books.");
    }

    @Transactional
    public List<Trade> processOrder(Order newOrder) {
        clientService.validateOrder(newOrder);

        OrderBook book = orderBooks.computeIfAbsent(newOrder.getTicker(), ticker -> new OrderBook(ticker));

        List<Trade> executedTrades = new ArrayList<>();
        List<Order> modifiedBookOrders = new ArrayList<>();

        if (newOrder.getSide() == Side.BUY) {
            matchBuyOrder(newOrder, book, executedTrades, modifiedBookOrders);
        } else {
            matchSellOrder(newOrder, book, executedTrades, modifiedBookOrders);
        }

        if (newOrder.getRemainingQuantity() > 0) {
            addOrderToBook(newOrder, book);
        }

        saveChanges(newOrder, modifiedBookOrders, executedTrades);

        return executedTrades;
    }

    private void matchBuyOrder(Order buyOrder, OrderBook book, List<Trade> executedTrades, List<Order> modifiedBookOrders) {
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
                    iterator.remove();
                }
            }
        }
    }

    private void matchSellOrder(Order sellOrder, OrderBook book, List<Trade> executedTrades, List<Order> modifiedBookOrders) {
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
                    iterator.remove();
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
        List<Order> allModifiedOrders = new ArrayList<>(modifiedBookOrders);
        allModifiedOrders.add(newOrder);

        for (Order order : allModifiedOrders) {
            if (order.getRemainingQuantity() == 0) {
                order.setStatus(OrderStatus.FILLED);
            } else if (order.getRemainingQuantity() < order.getOriginalQuantity()) {
                order.setStatus(OrderStatus.PARTIALLY_FILLED);
            }
        }

        orderRepository.saveAll(allModifiedOrders);
        if (!executedTrades.isEmpty()) {
            tradeRepository.saveAll(executedTrades);
            for (Trade trade : executedTrades) {
                clientService.updateHolding(trade.getSellClientId(), trade.getTicker(), BigDecimal.valueOf(trade.getQuantity()).negate());
                clientService.updateHolding(trade.getSellClientId(), "USDT", trade.getPrice().multiply(BigDecimal.valueOf(trade.getQuantity())));
                clientService.updateHolding(trade.getBuyClientId(), trade.getTicker(), BigDecimal.valueOf(trade.getQuantity()));
                clientService.updateHolding(trade.getBuyClientId(), "USDT", trade.getPrice().multiply(BigDecimal.valueOf(trade.getQuantity())).negate());
            }
        }
    }
}
