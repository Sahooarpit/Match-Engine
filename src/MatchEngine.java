import lombok.NoArgsConstructor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor
public class MatchEngine {

    private final Map<Ticker, OrderBook> orderBooks = new ConcurrentHashMap<>();
    private final List<Trade> trades = new ArrayList<>();

    public void processOrder(Order order) {

    }
}
