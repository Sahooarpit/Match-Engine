import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicLong;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Trade {

    @Transient
    private static final AtomicLong tradeIdGenerator = new AtomicLong();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String tradeId;
    private String buyOrderId;
    private String sellOrderId;
    private String buyClientId;
    private String sellClientId;
    private Ticker ticker;
    private Long quantity;
    private BigDecimal price;
    private Timestamp timestamp;



    public Trade(String buyOrderId, String sellOrderId, String buyClientId, String sellClientId, Ticker ticker, Long quantity, BigDecimal price) {
        this.tradeId = String.valueOf(tradeIdGenerator.getAndIncrement());
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.buyClientId = buyClientId;
        this.sellClientId = sellClientId;
        this.ticker = ticker;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = new Timestamp(System.currentTimeMillis());

    }
}