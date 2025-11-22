import lombok.NoArgsConstructor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor
public class MatchEngine {

    private final Map<Ticker, OrderBook> orderBooks = new ConcurrentHashMap<>();
    private final List<Trade> trades = new ArrayList<>();

    public void processOrder(Order order) {
        OrderBook orderBook = orderBooks.computeIfAbsent(order.getTicker(), t -> new OrderBook(t));
        if (order.getSide() == Side.BUY) {
            processBuyOrder(order, orderBook);
        } else {
            processSellOrder(order, orderBook);
        }
    }

    private void processBuyOrder(Order buyOrder, OrderBook orderBook) {
        Deque<Order> sellOrders = (Deque<Order>) orderBook.getAsks();
        if (sellOrders.isEmpty()){
            orderBook.getBids().putIfAbsent(buyOrder.getPrice(), new LinkedList<>());
        }
// TOD0: NEED TO MAKE CLEAR THAT THE PRICE ON A ORDER, IS THE PRICE OF THE ORDER OR THE PRICE OF THE TICKER,
// I THINK SHOULD BE PRICE OF ORDER
        if (sellOrders.peek().getPrice().compareTo(buyOrder.getPrice()) <= 0){
            Order sellOrder = sellOrders.getLast();
            if (sellOrder.getQuantity() >= buyOrder.getQuantity()){
                sellOrder.setQuantity(sellOrder.getQuantity() - buyOrder.getQuantity());

        }


    }
    }

    private void processSellOrder(Order sellOrder, OrderBook orderBook) {
        Deque<Order> buyOrders = (Deque<Order>) orderBook.getBids();
    }
}
