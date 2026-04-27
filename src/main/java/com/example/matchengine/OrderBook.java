package com.example.matchengine;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Deque;
import java.util.TreeMap;

@Getter
public class OrderBook {
    private final TreeMap<BigDecimal, Deque<Order>> bids = new TreeMap<>(Collections.reverseOrder());
    private final TreeMap<BigDecimal, Deque<Order>> asks = new TreeMap<>();
    private final String ticker;

    public OrderBook(String ticker) { // Changed from Ticker enum to String
        this.ticker = ticker;
    }
}
