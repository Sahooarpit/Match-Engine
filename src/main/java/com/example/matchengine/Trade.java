package com.example.matchengine;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; // Added Setter for JPA

import java.math.BigDecimal;
import java.time.Instant; // Changed from java.sql.Timestamp
import java.util.UUID; // Using UUID for tradeId
import java.util.concurrent.atomic.AtomicLong; // Still keeping for constructor logic, but JPA will handle ID

@Entity
@Table(name = "trades") // Explicitly name the table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter // Added Setter for JPA to work properly
public class Trade {

    @Transient // Mark as transient so JPA doesn't try to persist this
    private static final AtomicLong tradeIdGenerator = new AtomicLong(); // Consider removing if using UUID for ID generation

    @Id
    private String tradeId; // Using String for UUID

    private String buyOrderId;
    private String sellOrderId;
    private String buyClientId;
    private String sellClientId;

    @Enumerated(EnumType.STRING) // Store enum as String in DB
    private Ticker ticker;

    private Long quantity;

    @Column(precision = 19, scale = 4) // Example precision and scale for BigDecimal
    private BigDecimal price;

    private Instant timestamp; // Changed to Instant

    // Constructor for creating new trades, generating UUID for tradeId
    public Trade(String buyOrderId, String sellOrderId, String buyClientId, String sellClientId, Ticker ticker, Long quantity, BigDecimal price) {
        this.tradeId = UUID.randomUUID().toString(); // Generate UUID for tradeId
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.buyClientId = buyClientId;
        this.sellClientId = sellClientId;
        this.ticker = ticker;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = Instant.now(); // Set timestamp to now
    }
}
