package com.ctdecomerce.store.orders.model;

import com.ctdecomerce.store.cart.model.CartModel;
import com.ctdecomerce.store.user.model.UserModel;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "orders")
public class OrdersModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne()
    @JoinColumn()
    private UserModel user;

    @ManyToMany()
    @JoinTable()
    private List<CartModel> cart;

    @Column()
    private String status;
}