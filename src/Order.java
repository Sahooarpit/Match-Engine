import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@AllArgsConstructor
public class Order {
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
    private Timestamp timestamp;

    @Override
    public String toString() {
        return super.toString();
    }
}
