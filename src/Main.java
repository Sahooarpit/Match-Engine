import java.math.BigDecimal;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        MatchEngine matchEngine = new MatchEngine();

        Order buyOrder1 = new Order("client1", Ticker.GOOG, Side.BUY, 10L, new BigDecimal("100.50"));
        Order buyOrder2 = new Order("client2", Ticker.GOOG, Side.BUY, 5L, new BigDecimal("100.60"));
        Order sellOrder1 = new Order("client3", Ticker.GOOG, Side.SELL, 8L, new BigDecimal("100.55"));
        Order sellOrder2 = new Order("client4", Ticker.GOOG, Side.SELL, 12L, new BigDecimal("100.70"));

        System.out.println("Processing orders for GOOG...");
        List<Trade> trades1 = matchEngine.processOrder(buyOrder1);
        List<Trade> trades2 = matchEngine.processOrder(buyOrder2);
        List<Trade> trades3 = matchEngine.processOrder(sellOrder1);
        List<Trade> trades4 = matchEngine.processOrder(sellOrder2);

        System.out.println("Trades executed:");
        trades1.forEach(System.out::println);
        trades2.forEach(System.out::println);
        trades3.forEach(System.out::println);
        trades4.forEach(System.out::println);
    }
}
