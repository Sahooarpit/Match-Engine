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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buy_client_id")
    private Client buyClient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sell_client_id")
    private Client sellClient;

    @Column(name = "ticker", length = 10, nullable = false)
    private String ticker; // Changed from Ticker enum to String

    private Long quantity;

    @Column(precision = 19, scale = 4)
    private BigDecimal price;

    private Instant timestamp;

    public Trade(String buyOrderId, String sellOrderId, Client buyClient, Client sellClient, String ticker, Long quantity, BigDecimal price) {
        this.tradeId = UUID.randomUUID().toString();
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.buyClient = buyClient;
        this.sellClient = sellClient;
        this.ticker = ticker;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = Instant.now();
    }
}