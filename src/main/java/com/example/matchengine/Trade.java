package com.example.matchengine;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "trades")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Trade {

    @Id
    private String tradeId;

    private String buyOrderId;
    private String sellOrderId;
    private String buyClientId;
    private String sellClientId;

    @Column(name = "ticker", length = 10, nullable = false)
    private String ticker; // Changed from Ticker enum to String

    private Long quantity;

    @Column(precision = 19, scale = 4)
    private BigDecimal price;

    private Instant timestamp;

    public Trade(String buyOrderId, String sellOrderId, String buyClientId, String sellClientId, String ticker, Long quantity, BigDecimal price) {
        this.tradeId = UUID.randomUUID().toString();
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.buyClientId = buyClientId;
        this.sellClientId = sellClientId;
        this.ticker = ticker;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = Instant.now();
    }
}
