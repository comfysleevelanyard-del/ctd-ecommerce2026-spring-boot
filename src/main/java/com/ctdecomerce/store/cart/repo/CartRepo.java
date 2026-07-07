package com.ctdecomerce.store.cart.repo;

import com.ctdecomerce.store.cart.model.CartModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CartRepo extends JpaRepository<CartModel, UUID> {
    @Query("SELECT c FROM cart c WHERE c.userId = :userId ORDER BY c.id ASC")
    List<CartModel> findCartModelsByUserId(@Param("userId") String userId);
}
