package com.example.matchengine;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

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

class PortfolioHoldingId implements Serializable {
    private String client; // Must match the field name in PortfolioHolding
    private Ticker ticker; // Must match the field name in PortfolioHolding

    // equals and hashCode are essential for composite keys
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioHoldingId that = (PortfolioHoldingId) o;
        return Objects.equals(client, that.client) && ticker == that.ticker;
    }

    @Override
    public int hashCode() {
        return Objects.hash(client, ticker);
    }
}
