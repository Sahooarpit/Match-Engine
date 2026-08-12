package com.example.matchengine.controller;

import com.example.matchengine.Client;
import com.example.matchengine.MatchEngine;
import com.example.matchengine.Order;
import com.example.matchengine.Trade;
import com.example.matchengine.dto.OrderRequest;
import com.example.matchengine.repository.ClientRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TradeController {

    private final MatchEngine matchEngine;
    private final ClientRepository clientRepository;

    @PostMapping("/trade")
    public ResponseEntity<String> submitOrder(@Valid @RequestBody OrderRequest orderRequest) {
        String username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Client client = clientRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Client not found for username: " + username));

        Order order = new Order(
                client,
                orderRequest.getTicker(),
                orderRequest.getSide(),
                orderRequest.getQuantity(),
                orderRequest.getPrice()
        );

        List<Trade> trades = matchEngine.processOrder(order);
        String responseMessage = "Order processed. " + trades.size() + " trade(s) executed.";
        return ResponseEntity.ok(responseMessage);
    }
}