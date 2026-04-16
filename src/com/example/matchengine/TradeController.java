package com.example.matchengine;

import com.example.matchengine.api.TradeApi;
import com.example.matchengine.model.TradeRequest;
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
    public ResponseEntity<String> submitOrder(TradeRequest tradeRequest) {
        Order order = new Order(
                tradeRequest.getClientId(),
                Ticker.valueOf(tradeRequest.getTicker().getValue()),
                Side.valueOf(tradeRequest.getSide().getValue()),
                tradeRequest.getQuantity(),
                BigDecimal.valueOf(tradeRequest.getPrice())
        );
        List<com.example.matchengine.Trade> trades = matchEngine.processOrder(order);
        String responseMessage = "Order processed. " + trades.size() + " trade(s) executed.";
        return ResponseEntity.ok(responseMessage);
    }
}
