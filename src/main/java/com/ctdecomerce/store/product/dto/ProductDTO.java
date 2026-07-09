package com.ctdecomerce.store.product.dto;

import com.ctdecomerce.store.retailers.model.RetailersModel;
import lombok.*;

import java.util.UUID;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private UUID id;
    private String name;
    private RetailersModel owner;
}
