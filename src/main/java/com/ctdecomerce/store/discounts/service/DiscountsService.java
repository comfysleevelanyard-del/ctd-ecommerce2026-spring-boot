package com.ctdecomerce.store.discounts.service;

import com.ctdecomerce.store.discounts.dto.*;
import com.ctdecomerce.store.discounts.model.DiscountsModel;
import com.ctdecomerce.store.discounts.repository.DiscountsRepo;
import com.ctdecomerce.store.product.dto.ProductDTO;
import com.ctdecomerce.store.product.model.ProductModel;
import com.ctdecomerce.store.product.repository.ProductRepo;
import com.ctdecomerce.store.retailers.model.RetailersModel;
import com.ctdecomerce.store.retailers.repository.RetailersRepo;
import com.ctdecomerce.store.user.model.UserModel;
import com.ctdecomerce.store.user.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Setter
@RequiredArgsConstructor
@Service
public class DiscountsService {
    private final DiscountsRepo discountsRepo;
    private final ProductRepo productRepo;
    private final UserRepo userRepo;
    private final RetailersRepo retailersRepo;

    @Transactional
    public DiscountsModel getDiscountsByProduct(FindByProduct productStrings) {
        UUID productIdAsUUID = UUID.fromString(productStrings.getProductId());
        ProductModel product = productRepo.findById(productIdAsUUID).orElse(null);
        return discountsRepo.findDiscountsModelByProduct(product);
    }

    @Transactional
    public DiscountsModel createDicount(CreateDiscount createDiscount) {
        DiscountsModel discount = new DiscountsModel();
        discount.setName(createDiscount.getName());
        UserModel user = userRepo.findUserModelByUserId(createDiscount.getUserId());
        RetailersModel retailer = retailersRepo.findRetailerByUser(user);
        discount.setRetailer(retailer);
        discount.setOffer(createDiscount.getOffer());
        ProductModel product = productRepo.findById(UUID.fromString(createDiscount.getProductId())).orElse(null);
        discount.setProduct(product);
        return discountsRepo.save(discount);
    }

    @Transactional
    public List<DiscountsModel> getDiscounts(UserIdRequest userIdRequest) {
        UserModel user = userRepo.findUserModelByUserId(userIdRequest.getUserId());
        RetailersModel retailer = retailersRepo.findRetailerByUser(user);
        return discountsRepo.findDiscountsModelsByRetailer(retailer);
    }

    @Transactional
    public void deleteDiscount(DeleteRequest deleteRequest) {
        DiscountsModel discount = discountsRepo.findById(UUID.fromString(deleteRequest.getId())).orElse(null);
        assert discount != null;
        discountsRepo.delete(discount);
    }

    @Transactional
    public DiscountsModel getById(IdRequest idRequest) {
        return discountsRepo.findById(UUID.fromString(idRequest.getId())).orElse(null);
    }

    @Transactional
    public DiscountsModel changeName(ChangeName changeName) {
        DiscountsModel discount = discountsRepo.findById(UUID.fromString(changeName.getId())).orElse(null);
        assert discount != null;
        discount.setName(changeName.getName());
        return discountsRepo.save(discount);
    }

    @Transactional
    public DiscountsModel changeOffer(ChangeOffer changeOffer) {
        DiscountsModel discount = discountsRepo.findById(UUID.fromString(changeOffer.getId())).orElse(null);
        assert discount != null;
        discount.setOffer(changeOffer.getOffer());
        return discountsRepo.save(discount);
    }
}
