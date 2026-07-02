package com.ctdecomerce.store.product.dto;

public record CreateProductDTO(String name, String description, int priceInCents, String userId) {
}
