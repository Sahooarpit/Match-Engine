package com.example.matchengine.service;

import com.example.matchengine.Order;
import com.example.matchengine.OrderStatus;
import com.example.matchengine.PortfolioHolding;
import com.example.matchengine.repository.OrderRepository;
import com.example.matchengine.repository.PortfolioHoldingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioHoldingRepository portfolioHoldingRepository;
    private final OrderRepository orderRepository;

    public List<PortfolioHolding> getPortfolio(String clientId) {
        return portfolioHoldingRepository.findAllByClientClientId(clientId);
    }

    public List<Order> getPendingOrders(String clientId) {
        return orderRepository.findByClient_ClientIdAndStatusIn(clientId,
                List.of(OrderStatus.OPEN, OrderStatus.PARTIALLY_FILLED));
    }
}