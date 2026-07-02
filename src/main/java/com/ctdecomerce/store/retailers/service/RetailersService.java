package com.ctdecomerce.store.retailers.service;

import com.ctdecomerce.store.retailers.model.RetailersModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class RetailersService {
    private final RetailersModel retailersModel;

    public RetailersService (RetailersModel retailersModel) {
        this.retailersModel = retailersModel;
    }
}
