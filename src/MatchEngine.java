import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Math.min;

@NoArgsConstructor
public class MatchEngine {

    private final Map<Ticker, OrderBook> orderBooks = new ConcurrentHashMap<>();
    private final List<Trade> tradeHistory = new ArrayList<>();

    public synchronized List<Trade> processOrder(Order order) {
        OrderBook orderBook = orderBooks.computeIfAbsent(order.getTicker(), t -> new OrderBook(t));
        if (order.getSide() == Side.BUY) {
            return processBuyOrder(order, orderBook);
        } else {
            return processSellOrder(order, orderBook);
        }
    }

    private List<Trade> processBuyOrder(Order buyOrder, OrderBook orderBook) {
        List<Trade> trades = new ArrayList<>();

        TreeMap<BigDecimal, Deque<Order>> sellOrders = orderBook.getAsks();
        Iterator<Map.Entry<BigDecimal, Deque<Order>>> it = sellOrders.entrySet().iterator();

        while (it.hasNext() && buyOrder.getQuantity() > 0){
            Map.Entry<BigDecimal, Deque<Order>> entry = it.next();
            BigDecimal askPrice = entry.getKey();
            Deque<Order> ordersAtAskPrice = entry.getValue();

            if (buyOrder.getPrice().compareTo(askPrice) < 0){
                break;
        }

            Iterator<Order> ordersIterator = ordersAtAskPrice.iterator();
            while (ordersIterator.hasNext() && buyOrder.getQuantity() > 0) {
                Order sellOrder = ordersIterator.next();
                long matchQuantity = min(buyOrder.getQuantity(), sellOrder.getQuantity());

                buyOrder.setQuantity(buyOrder.getQuantity() - matchQuantity);
                sellOrder.setQuantity(sellOrder.getQuantity() - matchQuantity);

                Trade trade = new Trade(
                        buyOrder.getOrderId(),
                        sellOrder.getOrderId(),
                        buyOrder.getClientId(),
                        sellOrder.getClientId(),
                        buyOrder.getTicker(),
                        matchQuantity,
                        askPrice);

                trades.add(trade);
                tradeHistory.add(trade);
                System.out.println("   " + trade);
                if (sellOrder.getQuantity() == 0) {
                    ordersIterator.remove();
                }
            }
            if (ordersAtAskPrice.isEmpty()) {
                it.remove();
            }
        }
        if (buyOrder.getQuantity() > 0) {
            orderBook.getBids().computeIfAbsent(buyOrder.getPrice(), p -> new ArrayDeque<>()).add(buyOrder);
            System.out.println("Placed remaining buys Orders in book:" + buyOrder.getQuantity());

        }
        return trades;
    }

    private List<Trade> processSellOrder(Order sellOrder, OrderBook orderBook) {
        List<Trade> Trades = new ArrayList<>();
        TreeMap<BigDecimal, Deque<Order>> buyOrders = orderBook.getBids();
        Iterator<Map.Entry<BigDecimal, Deque<Order>>> it = buyOrders.entrySet().iterator();

        while (it.hasNext() && sellOrder.getQuantity() > 0) {
            Map.Entry<BigDecimal, Deque<Order>> entry = it.next();
            BigDecimal bidPrice = entry.getKey();
            Deque<Order> ordersAtBidPrice = entry.getValue();

            if (sellOrder.getPrice().compareTo(bidPrice) > 0) {
                break;
            }

            Iterator<Order> ordersIterator = ordersAtBidPrice.iterator();
            while (ordersIterator.hasNext() && sellOrder.getQuantity() > 0) {
                Order buyOrder = ordersIterator.next();
                long matchQuantity = min(sellOrder.getQuantity(), buyOrder.getQuantity());

                sellOrder.setQuantity(sellOrder.getQuantity() - matchQuantity);
                buyOrder.setQuantity(buyOrder.getQuantity() - matchQuantity);
                Trade trade = new Trade(
                        buyOrder.getOrderId(),
                        sellOrder.getOrderId(),
                        buyOrder.getClientId(),
                        sellOrder.getClientId(),
                        buyOrder.getTicker(),
                        matchQuantity,
                        bidPrice);
                Trades.add(trade);
                tradeHistory.add(trade);
                System.out.println("   " + trade);
                if (buyOrder.getQuantity() == 0) {
                    ordersIterator.remove();
                }
            }
            if (ordersAtBidPrice.isEmpty()) {
                it.remove();
            }
        }
        if (sellOrder.getQuantity() > 0) {
            orderBook.getAsks().computeIfAbsent(sellOrder.getPrice(), p -> new ArrayDeque<>()).add(sellOrder);
            System.out.println("Placed remaining sell Orders in book:" + sellOrder.getQuantity());

        }
        return Trades;

    }
}