package com.example.matchengine;

import com.example.matchengine.repository.ClientRepository;
import com.example.matchengine.repository.PortfolioHoldingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final PortfolioHoldingRepository portfolioHoldingRepository;

    @Transactional
    public Client findOrCreateClient(String clientId) {
        return clientRepository.findById(clientId)
                .orElseGet(() -> {
                    Client newClient = new Client(clientId); // Use clientId as name for simplicity
                    newClient.setClientId(clientId); // Ensure ID matches
                    return clientRepository.save(newClient);
                });
    }

    public void validateOrder(Order order) {
        Client client = findOrCreateClient(order.getClientId()); // Ensure client exists

        if (order.getSide() == Side.BUY) {
            // Check for sufficient cash balance (using USDT as proxy)
            BigDecimal requiredCash = order.getPrice().multiply(BigDecimal.valueOf(order.getOriginalQuantity()));
            PortfolioHolding cashHolding = portfolioHoldingRepository.findByClientClientIdAndTicker(order.getClientId(), Ticker.USDT)
                    .orElse(new PortfolioHolding(client, Ticker.USDT, BigDecimal.ZERO));

            if (cashHolding.getQuantity().compareTo(requiredCash) < 0) {
                throw new IllegalStateException("Insufficient funds for client " + order.getClientId() + ". Required: " + requiredCash + ", Available: " + cashHolding.getQuantity());
            }
        } else { // SELL order
            // Check for sufficient stock holding
            PortfolioHolding stockHolding = portfolioHoldingRepository.findByClientClientIdAndTicker(order.getClientId(), order.getTicker())
                    .orElse(new PortfolioHolding(client, order.getTicker(), BigDecimal.ZERO));

            if (stockHolding.getQuantity().compareTo(BigDecimal.valueOf(order.getOriginalQuantity())) < 0) {
                throw new IllegalStateException("Insufficient stock for client " + order.getClientId() + ". Required: " + order.getOriginalQuantity() + ", Available: " + stockHolding.getQuantity());
            }
        }
    }

    @Transactional
    public void updateHolding(String clientId, Ticker ticker, BigDecimal quantityChange) {
        Client client = findOrCreateClient(clientId); // Ensure client exists

        Optional<PortfolioHolding> existingHolding = portfolioHoldingRepository.findByClientClientIdAndTicker(clientId, ticker);

        PortfolioHolding holding;
        if (existingHolding.isPresent()) {
            holding = existingHolding.get();
            holding.setQuantity(holding.getQuantity().add(quantityChange));
        } else {
            // Create new holding if it doesn't exist
            holding = new PortfolioHolding(client, ticker, quantityChange);
        }
        // Ensure quantity doesn't go negative (unless it's a short sell, which we're not handling yet)
        if (holding.getQuantity().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Attempted to create negative holding for client " + clientId + " for ticker " + ticker);
        }
        portfolioHoldingRepository.save(holding);
    }
}
