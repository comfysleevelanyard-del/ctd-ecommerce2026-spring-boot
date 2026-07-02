package com.ctdecomerce.store.product.service;

import com.ctdecomerce.store.product.dto.CreateProductDTO;
import com.ctdecomerce.store.product.model.ProductModel;
import com.ctdecomerce.store.product.repository.ProductRepo;
import com.ctdecomerce.store.retailers.model.RetailersModel;
import com.ctdecomerce.store.retailers.repository.RetailersRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepo productRepo;
    private final RetailersRepo retailersRepo;

    public ProductService(ProductRepo productRepo, RetailersRepo retailersRepo) {
        this.productRepo = productRepo;
        this.retailersRepo = retailersRepo;
    }

    @Transactional
    public ProductModel createProduct(CreateProductDTO createProductDTO) {
        ProductModel productModel = new ProductModel();
        productModel.setName(createProductDTO.name());
        productModel.setDescription(createProductDTO.description());
        productModel.setPriceInCents(createProductDTO.priceInCents());
        RetailersModel retailersModel = retailersRepo.findRetailerById(UUID.fromString(createProductDTO.userId()));
        productModel.setOwner(retailersModel);
        productRepo.save(productModel);
        return productModel;
    }
}
