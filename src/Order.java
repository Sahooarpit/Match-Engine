import lombok.AllArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
public class Order {
    private String orderId;
    private String clientId;
    private Ticker ticker;
    private Side side;
    private Long quantity;
    private Long price;
    private Timestamp timestamp;

    @Override
    public String toString() {
        return super.toString();
    }
}
