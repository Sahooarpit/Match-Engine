package com.example.matchengine.repository;

import com.example.matchengine.Order;
import com.example.matchengine.OrderStatus;
import com.example.matchengine.Ticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    /**
     * Finds all open orders for a specific ticker, which is needed to build the order book.
     * Orders are sorted by price (desc for bids, asc for asks) and then by time.
     */
    List<Order> findByTickerAndStatusInOrderByPriceDescCreatedAtAsc(Ticker ticker, List<OrderStatus> statuses);
    List<Order> findByTickerAndStatusInOrderByPriceAscCreatedAtAsc(Ticker ticker, List<OrderStatus> statuses);

    /**
     * Finds all orders with a given status, used to load the order books on startup.
     */
    List<Order> findByStatusIn(List<OrderStatus> statuses);
}
