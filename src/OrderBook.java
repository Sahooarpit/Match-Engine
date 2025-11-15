import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.TreeMap;

public class OrderBook {
    private final TreeMap<BigDecimal, Deque<Order>> bids = new TreeMap<>(Collections.reverseOrder());
    private final TreeMap<BigDecimal, Deque<Order>> asks = new TreeMap<>();
    private final Ticker ticker;

    public OrderBook(Ticker ticker) {
        this.ticker = ticker;
    }

}
