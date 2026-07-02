package com.ctdecomerce.store.retailers.repository;

import com.ctdecomerce.store.retailers.model.RetailersModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface RetailersRepo extends JpaRepository<RetailersModel, UUID> {
    RetailersModel findRetailerById(UUID id);
}
