package com.example.matchengine.controller;

import com.example.matchengine.Client;
import com.example.matchengine.ClientService;
import com.example.matchengine.OrderStatus;
import com.example.matchengine.model.BalanceRequest;
import com.example.matchengine.model.Order;
import com.example.matchengine.model.PortfolioHolding;
import com.example.matchengine.repository.ClientRepository;
import com.example.matchengine.repository.OrderRepository;
import com.example.matchengine.repository.PortfolioHoldingRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class PortfolioController {

    private final ClientService clientService;
    private final PortfolioHoldingRepository portfolioHoldingRepository;
    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;

    public ResponseEntity<PortfolioHolding> addBalance(@Valid @RequestBody BalanceRequest balanceRequest) {
        String username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Client client = clientRepository.findByUsername(username).get();
        clientService.updateHolding(client.getClientId(), balanceRequest.getTicker(), BigDecimal.valueOf(balanceRequest.getQuantity()));

        PortfolioHolding response = new PortfolioHolding();
        response.setTicker(balanceRequest.getTicker());
        response.setQuantity(balanceRequest.getQuantity());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<List<PortfolioHolding>> getPortfolio() {
        String username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Client client = clientRepository.findByUsername(username).get();
        List<com.example.matchengine.PortfolioHolding> holdings = portfolioHoldingRepository.findAllByClientClientId(client.getClientId());
        List<PortfolioHolding> response = holdings.stream()
                .map(h -> {
                    PortfolioHolding ph = new PortfolioHolding();
                    ph.setTicker(h.getTicker());
                    ph.setQuantity(h.getQuantity().doubleValue());
                    return ph;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<List<Order>> getPendingOrders() {
        String username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Client client = clientRepository.findByUsername(username).get();
        List<com.example.matchengine.Order> pendingOrders = orderRepository.findByClientIdAndStatusIn(client.getClientId(),
                List.of(OrderStatus.OPEN, OrderStatus.PARTIALLY_FILLED));

        List<Order> response = pendingOrders.stream()
                .map(o -> {
                    Order order = new Order();
                    order.setOrderId(o.getOrderId());
                    order.setTicker(o.getTicker());
                    order.setSide(o.getSide().toString());
                    order.setPrice(o.getPrice().doubleValue());
                    order.setOriginalQuantity(o.getOriginalQuantity());
                    order.setRemainingQuantity(o.getRemainingQuantity());
                    order.setStatus(o.getStatus().toString());
                    order.setCreatedAt(OffsetDateTime.parse(o.getCreatedAt().toString()));
                    return order;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}