package com.ctdecomerce.store.cart.service;

import com.ctdecomerce.store.cart.dto.AddToCart;
import com.ctdecomerce.store.cart.dto.CartDTO;
import com.ctdecomerce.store.cart.dto.UpdateQuantityRequest;
import com.ctdecomerce.store.cart.dto.UserIdRequest;
import com.ctdecomerce.store.cart.model.CartModel;
import com.ctdecomerce.store.cart.repo.CartRepo;
import com.ctdecomerce.store.categories.repository.CategoriesRepo;
import com.ctdecomerce.store.discounts.model.DiscountsModel;
import com.ctdecomerce.store.discounts.repository.DiscountsRepo;
import com.ctdecomerce.store.product.dto.OwnerDTO;
import com.ctdecomerce.store.product.dto.ProductDTO;
import com.ctdecomerce.store.product.model.ProductModel;
import com.ctdecomerce.store.product.repository.ProductRepo;
import com.ctdecomerce.store.user.model.UserModel;
import com.ctdecomerce.store.user.repository.UserRepo;
import com.stripe.model.Product;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Setter
@AllArgsConstructor
@Service
public class CartService {
    private final CartRepo cartRepo;
    private final ProductRepo productRepo;
    private final UserRepo userRepo;
    private final DiscountsRepo discountsRepo;
    private final CategoriesRepo categoriesRepo;


    @Transactional
    public CartModel addToCart(AddToCart addToCart) {
        UserModel user = userRepo.findUserModelByUserId(addToCart.getUserId());

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        ProductModel product = productRepo
                .findById(UUID.fromString(addToCart.getProductId()))
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartModel existingCartItem = cartRepo
                .findByUserAndProductAndShowingTrue(user, product)
                .orElse(null);

        if (existingCartItem != null) {
            if (existingCartItem.getQuantity() >= product.getStock()) {
                throw new RuntimeException("Cannot add more than available stock");
            }

            existingCartItem.setQuantity(existingCartItem.getQuantity() + 1);
            return cartRepo.save(existingCartItem);
        }

        CartModel newCartItem = new CartModel();
        newCartItem.setUser(user);
        newCartItem.setProduct(product);
        newCartItem.setQuantity(1);
        newCartItem.setShowing(true);

        return cartRepo.save(newCartItem);
    }

    @Transactional
    public List<CartDTO> getCart(UserIdRequest userIdRequest) {
        UserModel user = userRepo.findUserModelByUserId(userIdRequest.getUserId());
        List<CartModel> carts = cartRepo.findCartModelsByUserId(user, true);
        List<CartDTO> deserializedCarts = new ArrayList<>();
        for (CartModel cart : carts) {
            ProductDTO productFinal;
            ProductModel product = cart.getProduct();
            OwnerDTO owner = new OwnerDTO(product.getOwner().getId(), product.getOwner().getName());
            DiscountsModel discounts = discountsRepo.findDiscountsModelByProduct(product);
            if (discounts != null) {
                double productOgPrice = ((double) product.getPriceInCents() / 100) * (1 - discounts.getOffer()) * 100;
                productFinal = new ProductDTO(product.getId(), product.getName(), owner, (int) productOgPrice, true, product.getPriceInCents(), product.isShowing(), product.isAvailable(), product.getStock());
            } else {
                productFinal = new ProductDTO(product.getId(), product.getName(), owner, product.getPriceInCents(), false, product.getPriceInCents(),product.isShowing(),product.isAvailable(), product.getStock());
            }
            CartDTO cartDeserialized = new CartDTO(cart.getId(), cart.isShowing(), cart.getQuantity(), productFinal, carts.getFirst().getUser().getUserId());
            deserializedCarts.add(cartDeserialized);
        }
        return deserializedCarts;
    }

    @Transactional
    public CartModel incrementQuantity(UpdateQuantityRequest request) {
        CartModel cart = cartRepo.findById(UUID.fromString(request.getCartId()))
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.setQuantity(cart.getQuantity() + 1);
        return cartRepo.save(cart);
    }

    @Transactional
    public void decrementQuantity(UpdateQuantityRequest request) {
        CartModel cart = cartRepo.findById(UUID.fromString(request.getCartId()))
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        if (cart.getQuantity() > 1) {
            cart.setQuantity(cart.getQuantity() - 1);
            cartRepo.save(cart);
        } else {
            cartRepo.delete(cart);
        }

    }

}