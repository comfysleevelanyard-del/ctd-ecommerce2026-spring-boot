package com.ctdecomerce.store.retailers.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity(name = "retailers")
public class RetailersModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column()
    private String name;

    @Column()
    private String accountId;

    @Column()
    private List<String> products;

    @Column()
    private String userId;

    @Column()
    private Date dateCreated = new Date();
}
