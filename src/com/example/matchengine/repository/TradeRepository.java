package com.example.matchengine.repository;

import com.example.matchengine.Ticker;
import com.example.matchengine.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, String> { // <EntityType, IdType>

    // --- Method 1: Built-in Queries ---
    // You get these for free from JpaRepository:
    // - save(Trade trade) -> Saves a new trade or updates an existing one.
    // - findById(String tradeId) -> Finds a trade by its ID.
    // - findAll() -> Finds all trades.
    // - delete(Trade trade) -> Deletes a trade.
    // - and many more!

    // --- Method 2: Derived Queries ---
    // Spring Data JPA creates the query from the method name.
    // This becomes: "SELECT t FROM Trade t WHERE t.ticker = ?1"
    List<Trade> findByTicker(Ticker ticker);

    // This becomes: "SELECT t FROM Trade t WHERE t.buyClientId = ?1 AND t.timestamp > ?2"
    List<Trade> findByBuyClientIdAndTimestampAfter(String clientId, Instant timestamp);


    // --- Method 3: Custom Queries with @Query ---
    // For complex queries, you can write your own using JPQL (Java Persistence Query Language).
    @Query("SELECT t FROM Trade t WHERE t.ticker = :ticker AND (t.buyClientId = :clientId OR t.sellClientId = :clientId)")
    List<Trade> findTradesByClientAndTicker(String clientId, Ticker ticker);
}
