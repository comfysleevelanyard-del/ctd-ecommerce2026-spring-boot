package com.ctdecomerce.store.retailers.service;

import com.ctdecomerce.store.retailers.dto.ConnectedAccountDTO;
import com.ctdecomerce.store.retailers.dto.ConnectedAccountRequest;
import com.ctdecomerce.store.retailers.dto.IsRetailer;
import com.ctdecomerce.store.retailers.dto.UserIdRequest;
import com.ctdecomerce.store.retailers.model.RetailersModel;
import com.ctdecomerce.store.retailers.repository.RetailersRepo;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class RetailersService {
    private final RetailersRepo retailersRepo;

    public RetailersService(RetailersRepo retailersRepo) {
        this.retailersRepo = retailersRepo;
    }

    @Transactional
    public ConnectedAccountDTO createNewRetailer(ConnectedAccountRequest connectedAccountRequest) throws StripeException {
        AccountCreateParams accountCreateParams = AccountCreateParams
                .builder()
                .putMetadata("name", connectedAccountRequest.getName())
                .putMetadata("userId", connectedAccountRequest.getUserId())
                .setType(AccountCreateParams.Type.EXPRESS)
                .build();
        Account account = Account.create(accountCreateParams);

        AccountLinkCreateParams accountLinkCreateParams = AccountLinkCreateParams.builder()
                .setAccount(account.getId())
                .setRefreshUrl("http://localhost:3000")
                .setReturnUrl("http://localhost:3000")
                .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                .build();

        AccountLink accountLink = AccountLink.create(accountLinkCreateParams);
        return new ConnectedAccountDTO(accountLink.getUrl());
    }

    @Transactional
    public void createAccountToDB(String name, String accountId, String userId) {
        RetailersModel retailersModel = new RetailersModel();
        retailersModel.setName(name);
        retailersModel.setAccountId(accountId);
        retailersModel.setUserId(userId);
        retailersRepo.save(retailersModel);
    }

    @Transactional
    public IsRetailer checkIfRetailer(UserIdRequest userIdRequest) {
        try {
            var retailer = retailersRepo.findRetailerByUserId(userIdRequest.getUserId());
            return new IsRetailer(true);
        } catch (NoSuchElementException e) {
            return new IsRetailer(false);
        }
    }
}