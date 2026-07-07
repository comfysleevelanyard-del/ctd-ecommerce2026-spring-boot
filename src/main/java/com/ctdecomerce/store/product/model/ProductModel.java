package com.ctdecomerce.store.product.model;

import com.ctdecomerce.store.retailers.model.RetailersModel;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "products")
public class ProductModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column()
    private String name;

    @Column()
    private String description;

    @Column()
    private int priceInCents;

    @ManyToOne()
    @JoinColumn()
    private RetailersModel owner;
}
