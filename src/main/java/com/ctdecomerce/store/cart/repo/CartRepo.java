package com.ctdecomerce.store.cart.repo;

import com.ctdecomerce.store.cart.model.CartModel;
import com.ctdecomerce.store.product.model.ProductModel;
import com.ctdecomerce.store.user.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartRepo extends JpaRepository<CartModel, UUID> {
    @Query("SELECT c FROM cart c WHERE c.user = :userId and c.showing = :showing ORDER BY c.id ASC")
    List<CartModel> findCartModelsByUserId(@Param("userId") UserModel user, @Param("showing") boolean showing);
    Optional<CartModel> findByUserAndProductAndShowingTrue(
            UserModel user,
            ProductModel product
    );
}
