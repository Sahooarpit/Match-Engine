import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderBookTest {

    @Test
    void testOrderBookCreation() {
        OrderBook orderBook = new OrderBook(Ticker.GOOG);
        assertEquals(Ticker.GOOG, orderBook.getTicker());
        assertNotNull(orderBook.getBids());
        assertNotNull(orderBook.getAsks());
        assertTrue(orderBook.getBids().isEmpty());
        assertTrue(orderBook.getAsks().isEmpty());
    }
}
