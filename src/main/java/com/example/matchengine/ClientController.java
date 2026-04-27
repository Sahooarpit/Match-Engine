package com.example.matchengine;

import com.example.matchengine.api.ClientsApi;
import com.example.matchengine.model.BalanceRequest;
import com.example.matchengine.model.ClientRequest;
import com.example.matchengine.model.Order;
import com.example.matchengine.model.PortfolioHolding;
import com.example.matchengine.repository.OrderRepository;
import com.example.matchengine.repository.PortfolioHoldingRepository;
import jakarta.validation.Valid; // Changed to jakarta.validation.Valid
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ClientController implements ClientsApi {

    private final ClientService clientService;
    private final PortfolioHoldingRepository portfolioHoldingRepository;
    private final OrderRepository orderRepository;

    @Override
    public ResponseEntity<com.example.matchengine.model.Client> createClient(@Valid ClientRequest clientRequest) {
        Client client = clientService.findOrCreateClient(clientRequest.getClientId());

        com.example.matchengine.model.Client response = new com.example.matchengine.model.Client();
        response.setClientId(client.getClientId());
        response.setName(client.getName());
        response.setCreatedAt(OffsetDateTime.parse(client.getCreatedAt().toString()));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<PortfolioHolding> addBalance(String clientId, @Valid BalanceRequest balanceRequest) {
        clientService.updateHolding(clientId, balanceRequest.getTicker(), BigDecimal.valueOf(balanceRequest.getQuantity()));

        PortfolioHolding response = new PortfolioHolding();
        response.setTicker(balanceRequest.getTicker());
        response.setQuantity(balanceRequest.getQuantity());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<PortfolioHolding>> getPortfolio(String clientId) {
        List<com.example.matchengine.PortfolioHolding> holdings = portfolioHoldingRepository.findAllByClientClientId(clientId);
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

    @Override
    public ResponseEntity<List<Order>> getPendingOrders(String clientId) {
        List<com.example.matchengine.Order> pendingOrders = orderRepository.findByClientIdAndStatusIn(clientId,
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
