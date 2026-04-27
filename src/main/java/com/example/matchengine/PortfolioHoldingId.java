package com.example.matchengine;

import java.io.Serializable;
import java.util.Objects;

public class PortfolioHoldingId implements Serializable {
    private String client;
    private String ticker; // Changed from Ticker enum to String

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioHoldingId that = (PortfolioHoldingId) o;
        return Objects.equals(client, that.client) && Objects.equals(ticker, that.ticker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client, ticker);
    }
}
