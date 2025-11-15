import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@AllArgsConstructor
public class Trade {
    private String tradeId;
    private String buyOrderId;
    private String sellOrderId;
    private Ticker ticker;
    private Long quantity;
    private BigDecimal price;
    private Timestamp timestamp;

}
