package com.ctdecomerce.store.cart.model;

import com.ctdecomerce.store.product.model.ProductModel;
import com.ctdecomerce.store.user.model.UserModel;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "cart")
public class CartModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne()
    @JoinColumn
    private UserModel user;

    @Column()
    private boolean showing = true;

    @Column()
    private int quantity = 1;

    @ManyToOne()
    private ProductModel product;

    public boolean getShowing() {
        return this.showing;
    }
}
