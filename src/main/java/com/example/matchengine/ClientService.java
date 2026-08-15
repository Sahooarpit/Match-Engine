package com.example.matchengine;

import com.example.matchengine.repository.ClientRepository;
import com.example.matchengine.repository.InstrumentRepository;
import com.example.matchengine.repository.PortfolioHoldingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final PortfolioHoldingRepository portfolioHoldingRepository;
    private final InstrumentRepository instrumentRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Client registerClient(Client client) {
        if (clientRepository.findByUsername(client.getUsername()).isPresent()) {
            throw new IllegalStateException("Username is already taken!");
        }
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        return clientRepository.save(client);
    }

    public Optional<Client> findByUsername(String username) {
        return clientRepository.findByUsername(username);
    }

    public List<Client> findAllClients() {
        return clientRepository.findAll();
    }

    public void validateOrder(Order order) {
        Client client = order.getClient();

        instrumentRepository.findById(order.getTicker())
                .orElseThrow(() -> new IllegalStateException("Invalid ticker: " + order.getTicker()));

        if (order.getSide() == Side.BUY) {
            BigDecimal requiredCash = order.getPrice().multiply(BigDecimal.valueOf(order.getOriginalQuantity()));
            PortfolioHolding cashHolding = portfolioHoldingRepository.findByClientClientIdAndTicker(client.getClientId(), "USDT")
                    .orElse(new PortfolioHolding(client, "USDT", BigDecimal.ZERO));

            if (cashHolding.getQuantity().compareTo(requiredCash) < 0) {
                throw new IllegalStateException("Insufficient funds for client " + client.getClientId() + ". Required: " + requiredCash + ", Available: " + cashHolding.getQuantity());
            }
        } else { // SELL order
            PortfolioHolding stockHolding = portfolioHoldingRepository.findByClientClientIdAndTicker(client.getClientId(), order.getTicker())
                    .orElse(new PortfolioHolding(client, order.getTicker(), BigDecimal.ZERO));

            if (stockHolding.getQuantity().compareTo(BigDecimal.valueOf(order.getOriginalQuantity())) < 0) {
                throw new IllegalStateException("Insufficient stock for client " + client.getClientId() + ". Required: " + order.getOriginalQuantity() + ", Available: " + stockHolding.getQuantity());
            }
        }
    }

    @Transactional
    public void updateHolding(String clientId, String ticker, BigDecimal quantityChange) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalStateException("Client not found with ID: " + clientId));

        instrumentRepository.findById(ticker)
                .orElseThrow(() -> new IllegalStateException("Invalid ticker: " + ticker));

        PortfolioHolding holding = portfolioHoldingRepository.findByClientClientIdAndTicker(clientId, ticker)
                .orElse(new PortfolioHolding(client, ticker, BigDecimal.ZERO));

        BigDecimal newQuantity = holding.getQuantity().add(quantityChange);
        if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Attempted to create negative holding for client " + clientId + " for ticker " + ticker);
        }
        holding.setQuantity(newQuantity);
        portfolioHoldingRepository.save(holding);
    }
}