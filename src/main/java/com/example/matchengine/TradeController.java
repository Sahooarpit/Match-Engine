package com.example.matchengine;

import com.example.matchengine.model.TradeRequest;
import com.example.matchengine.repository.ClientRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TradeController {

    private final MatchEngine matchEngine;
    private final ClientRepository clientRepository;

    @PostMapping("/trade")
    public ResponseEntity<String> submitOrder(@Valid @RequestBody TradeRequest tradeRequest) {
        String username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Client client = clientRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Client not found for username: " + username));

        Order order = new Order(
                client,
                tradeRequest.getTicker(),
                Side.valueOf(tradeRequest.getSide().toString()),
                tradeRequest.getQuantity(),
                BigDecimal.valueOf(tradeRequest.getPrice())
        );
        List<Trade> trades = matchEngine.processOrder(order);
        String responseMessage = "Order processed. " + trades.size() + " trade(s) executed.";
        return ResponseEntity.ok(responseMessage);
    }
}