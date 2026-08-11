package com.example.matchengine.repository;

import com.example.matchengine.Client;
import com.example.matchengine.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, String> {
    List<Trade> findByBuyClientOrSellClient(Client buyClient, Client sellClient);
}