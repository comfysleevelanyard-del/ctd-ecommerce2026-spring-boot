package com.ctdecomerce.store.orders.repository;

import com.ctdecomerce.store.orders.model.OrdersModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrdersRepo extends JpaRepository<OrdersModel, UUID> {
}
