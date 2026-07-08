package com.ctdecomerce.store.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrdersRepo extends JpaRepository<OrdersRepo, UUID> {
}
