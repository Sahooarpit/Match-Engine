package com.example.matchengine;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "portfolio_holdings")
@Getter
@Setter
@NoArgsConstructor
@IdClass(PortfolioHoldingId.class) // Specifies composite primary key class
public class PortfolioHolding {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "ticker", length = 10)
    private Ticker ticker;

    @Column(nullable = false, precision = 19, scale = 8) // Using BigDecimal for quantity for precision
    private BigDecimal quantity;

    public PortfolioHolding(Client client, Ticker ticker, BigDecimal quantity) {
        this.client = client;
        this.ticker = ticker;
        this.quantity = quantity;
    }
}

// --- Composite Primary Key Class ---

