package com.example.matchengine.repository;

import com.example.matchengine.PortfolioHolding;
import com.example.matchengine.PortfolioHoldingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioHoldingRepository extends JpaRepository<PortfolioHolding, PortfolioHoldingId> {

    Optional<PortfolioHolding> findByClientClientIdAndTicker(String clientId, String ticker);

    List<PortfolioHolding> findAllByClientClientId(String clientId); // For getPortfolio endpoint
}
