package com.example.matchengine;

import com.example.matchengine.api.TradeApi;
import com.example.matchengine.model.TradeRequest;
import jakarta.validation.Valid; // Changed to jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class TradeController implements TradeApi {

    private final MatchEngine matchEngine;

    @Autowired
    public TradeController(MatchEngine matchEngine) {
        this.matchEngine = matchEngine;
    }

    @Override
    public ResponseEntity<String> submitOrder(@Valid TradeRequest tradeRequest) {
        // The Ticker enum is gone, so we pass the ticker as a String directly.
        Order order = new Order(
                tradeRequest.getClientId(),
                tradeRequest.getTicker(), // Pass the String directly
                Side.valueOf(tradeRequest.getSide().toString()), // Use toString() for enum conversion
                tradeRequest.getQuantity(),
                BigDecimal.valueOf(tradeRequest.getPrice())
        );
        List<Trade> trades = matchEngine.processOrder(order);
        String responseMessage = "Order processed. " + trades.size() + " trade(s) executed.";
        return ResponseEntity.ok(responseMessage);
    }
}
