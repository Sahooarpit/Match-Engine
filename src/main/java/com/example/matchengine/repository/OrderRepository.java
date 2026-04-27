package com.example.matchengine.repository;

import com.example.matchengine.Order;
import com.example.matchengine.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByTickerAndStatusInOrderByPriceDescCreatedAtAsc(String ticker, List<OrderStatus> statuses);
    List<Order> findByTickerAndStatusInOrderByPriceAscCreatedAtAsc(String ticker, List<OrderStatus> statuses);

    List<Order> findByStatusIn(List<OrderStatus> statuses);

    List<Order> findByClientIdAndStatusIn(String clientId, List<OrderStatus> statuses); // For getPendingOrders endpoint
}
