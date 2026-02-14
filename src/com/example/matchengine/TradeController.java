package com.example.matchengine;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/trade")
public class TradeController {

    private final MatchEngine matchEngine;

    @Autowired
    public TradeController(MatchEngine matchEngine) {
        this.matchEngine = matchEngine;
    }

    @Operation(summary = "Submit a new order", description = "Creates a new buy or sell order and matches it against the order book.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<List<Trade>> submitOrder(@RequestBody TradeRequest request) {
        Order order = new Order(
                request.getClientId(),
                request.getTicker(),
                request.getSide(),
                request.getQuantity(),
                request.getPrice()
        );
        List<Trade> trades = matchEngine.processOrder(order);
        return ResponseEntity.ok(trades);
    }
}
