import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicLong;

@AllArgsConstructor
public class Order {
    @Transient
    private static final AtomicLong tradeIdGenerator = new AtomicLong();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private String orderId;
    @Getter
    private String clientId;
    @Getter
    private Ticker ticker;
    @Getter
    private Side side;
    @Getter
    @Setter
    private long quantity;
    @Getter
    private BigDecimal price;
    @Getter
    private Timestamp timestamp;

    public Order(String clientId, Ticker ticker, Side side, long quantity, BigDecimal price) {
        this.orderId =String.valueOf(Order.tradeIdGenerator.getAndIncrement());
        this.clientId = clientId;
        this.ticker = ticker;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
