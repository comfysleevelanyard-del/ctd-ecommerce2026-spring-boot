package com.ctdecomerce.store.cart.dto;

import com.ctdecomerce.store.product.dto.ProductDTO;
import com.ctdecomerce.store.product.model.ProductModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartDTO {
    private UUID id;
    private boolean showing;
    private int quantity;
    private ProductDTO product;
    private String userId;
}
