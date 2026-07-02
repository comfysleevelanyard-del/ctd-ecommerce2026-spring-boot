package com.ctdecomerce.store.product.controller;

import com.ctdecomerce.store.product.dto.CreateProductDTO;
import com.ctdecomerce.store.product.model.ProductModel;
import com.ctdecomerce.store.product.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("ProductController")
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/create")
    public ResponseEntity<ProductModel> createProduct(@RequestBody CreateProductDTO createProductDTO) {
        return new ResponseEntity<>(productService.createProduct(createProductDTO), HttpStatus.CREATED);
    }
}
