package com.example.matchengine.controller;

import com.example.matchengine.Client;
import com.example.matchengine.Order;
import com.example.matchengine.OrderStatus;
import com.example.matchengine.PortfolioHolding;
import com.example.matchengine.repository.ClientRepository;
import com.example.matchengine.repository.OrderRepository;
import com.example.matchengine.repository.PortfolioHoldingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioHoldingRepository portfolioHoldingRepository;
    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;

    @GetMapping
    public ResponseEntity<List<PortfolioHolding>> getPortfolio() {
        String username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Client client = clientRepository.findByUsername(username).get();
        List<PortfolioHolding> holdings = portfolioHoldingRepository.findAllByClientClientId(client.getClientId());
        return ResponseEntity.ok(holdings);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getPendingOrders() {
        String username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Client client = clientRepository.findByUsername(username).get();
        List<Order> pendingOrders = orderRepository.findByClient_ClientIdAndStatusIn(client.getClientId(),
                List.of(OrderStatus.OPEN, OrderStatus.PARTIALLY_FILLED));
        return ResponseEntity.ok(pendingOrders);
    }
}