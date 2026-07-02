package com.ctdecomerce.store.retailers.controller;

import com.ctdecomerce.store.retailers.service.RetailersService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("RetailersController")
@RequestMapping("/retailers")
public class RetailersController {
    private final RetailersService retailersService;

    public RetailersController (RetailersService retailersService) {
        this.retailersService = retailersService;
    }
}
