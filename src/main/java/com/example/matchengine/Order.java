package com.example.matchengine;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders") // Use "orders" as the table name, as "order" is a reserved SQL keyword
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    private String orderId;

    private String clientId;

    @Enumerated(EnumType.STRING)
    private Ticker ticker;

    @Enumerated(EnumType.STRING)
    private Side side;

    @Column(precision = 19, scale = 8)
    private BigDecimal price;

    private long originalQuantity;
    private long remainingQuantity;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Instant createdAt;

    public Order(String clientId, Ticker ticker, Side side, long quantity, BigDecimal price) {
        this.orderId = UUID.randomUUID().toString();
        this.clientId = clientId;
        this.ticker = ticker;
        this.side = side;
        this.price = price;
        this.originalQuantity = quantity;
        this.remainingQuantity = quantity;
        this.status = OrderStatus.OPEN; // Default status for a new order
        this.createdAt = Instant.now();
    }
}

