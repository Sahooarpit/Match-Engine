package com.example.matchengine;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TradeRequest {
    private String clientId;
    private Ticker ticker;
    private Side side;
    private long quantity;
    private BigDecimal price;
}
