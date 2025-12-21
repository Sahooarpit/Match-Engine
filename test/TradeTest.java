import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class TradeTest {

    @Test
    void testTradeCreation() {
        Trade trade = new Trade("buyOrder1", "sellOrder1", "buyClient1", "sellClient1", Ticker.GOOG, 100L, new BigDecimal("2500.00"));

        assertNotNull(trade.getTradeId());
        assertEquals("buyOrder1", trade.getBuyOrderId());
        assertEquals("sellOrder1", trade.getSellOrderId());
        assertEquals("buyClient1", trade.getBuyClientId());
        assertEquals("sellClient1", trade.getSellClientId());
        assertEquals(Ticker.GOOG, trade.getTicker());
        assertEquals(100L, trade.getQuantity());
        assertEquals(new BigDecimal("2500.00"), trade.getPrice());
        assertNotNull(trade.getTimestamp());
    }
}
