package com.ctdecomerce.store.product.repository;

import com.ctdecomerce.store.product.model.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepo extends JpaRepository<ProductModel, UUID> {
}
