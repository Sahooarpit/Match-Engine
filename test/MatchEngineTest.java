import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchEngineTest {

    private MatchEngine matchEngine;

    @BeforeEach
    void setUp() {
        matchEngine = new MatchEngine();
    }

    @Test
    void testNoMatch() {
        Order buyOrder = new Order("client1", Ticker.GOOG, Side.BUY, 10L, new BigDecimal("100.00"));
        List<Trade> trades = matchEngine.processOrder(buyOrder);
        assertTrue(trades.isEmpty());
    }

    @Test
    void testSimpleMatch() {
        // Sell order first
        Order sellOrder = new Order("client1", Ticker.GOOG, Side.SELL, 10L, new BigDecimal("100.00"));
        matchEngine.processOrder(sellOrder);

        // Buy order matches
        Order buyOrder = new Order("client2", Ticker.GOOG, Side.BUY, 10L, new BigDecimal("100.00"));
        List<Trade> trades = matchEngine.processOrder(buyOrder);

        assertEquals(1, trades.size());
        Trade trade = trades.get(0);
        assertEquals(10L, trade.getQuantity());
        assertEquals(new BigDecimal("100.00"), trade.getPrice());
    }

    @Test
    void testPartialMatch() {
        // Sell order for 10
        Order sellOrder = new Order("client1", Ticker.GOOG, Side.SELL, 10L, new BigDecimal("100.00"));
        matchEngine.processOrder(sellOrder);

        // Buy order for 5
        Order buyOrder = new Order("client2", Ticker.GOOG, Side.BUY, 5L, new BigDecimal("100.00"));
        List<Trade> trades = matchEngine.processOrder(buyOrder);

        assertEquals(1, trades.size());
        assertEquals(5L, trades.get(0).getQuantity());
        
        // Remaining sell order should be 5. We can verify by sending another buy order.
        Order buyOrder2 = new Order("client3", Ticker.GOOG, Side.BUY, 5L, new BigDecimal("100.00"));
        List<Trade> trades2 = matchEngine.processOrder(buyOrder2);
        assertEquals(1, trades2.size());
        assertEquals(5L, trades2.get(0).getQuantity());
    }
    
    @Test
    void testPricePriority() {
        // Sell orders at different prices
        matchEngine.processOrder(new Order("c1", Ticker.GOOG, Side.SELL, 10L, new BigDecimal("101.00")));
        matchEngine.processOrder(new Order("c2", Ticker.GOOG, Side.SELL, 10L, new BigDecimal("100.00")));

        // Buy order should match with lower price first
        Order buyOrder = new Order("c3", Ticker.GOOG, Side.BUY, 10L, new BigDecimal("102.00"));
        List<Trade> trades = matchEngine.processOrder(buyOrder);

        assertEquals(1, trades.size());
        assertEquals(new BigDecimal("100.00"), trades.get(0).getPrice());
    }
}
