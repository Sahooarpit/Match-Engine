import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@AllArgsConstructor
public class Order {
    private String orderId;
    private String clientId;
    @Getter
    private Ticker ticker;
    @Getter
    private Side side;
    @Getter
    @Setter
    private Float quantity;
    @Getter
    private BigDecimal price;
    private Timestamp timestamp;

    @Override
    public String toString() {
        return super.toString();
    }
}
