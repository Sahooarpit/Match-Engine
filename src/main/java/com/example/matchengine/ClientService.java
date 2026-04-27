package com.example.matchengine;

import com.example.matchengine.repository.ClientRepository;
import com.example.matchengine.repository.InstrumentRepository;
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
    private final InstrumentRepository instrumentRepository; // Add InstrumentRepository

    @Transactional
    public Client findOrCreateClient(String clientId) {
        return clientRepository.findById(clientId)
                .orElseGet(() -> {
                    Client newClient = new Client(clientId);
                    newClient.setClientId(clientId);
                    return clientRepository.save(newClient);
                });
    }

    public void validateOrder(Order order) {
        Client client = findOrCreateClient(order.getClientId());

        // Validate that the instrument exists
        instrumentRepository.findById(order.getTicker())
                .orElseThrow(() -> new IllegalStateException("Invalid ticker: " + order.getTicker()));

        if (order.getSide() == Side.BUY) {
            BigDecimal requiredCash = order.getPrice().multiply(BigDecimal.valueOf(order.getOriginalQuantity()));
            PortfolioHolding cashHolding = portfolioHoldingRepository.findByClientClientIdAndTicker(order.getClientId(), "USDT")
                    .orElse(new PortfolioHolding(client, "USDT", BigDecimal.ZERO));

            if (cashHolding.getQuantity().compareTo(requiredCash) < 0) {
                throw new IllegalStateException("Insufficient funds for client " + order.getClientId() + ". Required: " + requiredCash + ", Available: " + cashHolding.getQuantity());
            }
        } else { // SELL order
            PortfolioHolding stockHolding = portfolioHoldingRepository.findByClientClientIdAndTicker(order.getClientId(), order.getTicker())
                    .orElse(new PortfolioHolding(client, order.getTicker(), BigDecimal.ZERO));

            if (stockHolding.getQuantity().compareTo(BigDecimal.valueOf(order.getOriginalQuantity())) < 0) {
                throw new IllegalStateException("Insufficient stock for client " + order.getClientId() + ". Required: " + order.getOriginalQuantity() + ", Available: " + stockHolding.getQuantity());
            }
        }
    }

    @Transactional
    public void updateHolding(String clientId, String ticker, BigDecimal quantityChange) {
        Client client = findOrCreateClient(clientId);

        // Validate that the instrument exists before updating a holding
        instrumentRepository.findById(ticker)
                .orElseThrow(() -> new IllegalStateException("Invalid ticker: " + ticker));

        Optional<PortfolioHolding> existingHolding = portfolioHoldingRepository.findByClientClientIdAndTicker(clientId, ticker);

        PortfolioHolding holding;
        if (existingHolding.isPresent()) {
            holding = existingHolding.get();
            holding.setQuantity(holding.getQuantity().add(quantityChange));
        } else {
            holding = new PortfolioHolding(client, ticker, quantityChange);
        }
        if (holding.getQuantity().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Attempted to create negative holding for client " + clientId + " for ticker " + ticker);
        }
        portfolioHoldingRepository.save(holding);
    }
}
