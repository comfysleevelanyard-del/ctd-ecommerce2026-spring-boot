package com.ctdecomerce.store.retailers.service;

import com.ctdecomerce.store.retailers.dto.ConnectedAccountDTO;
import com.ctdecomerce.store.retailers.dto.ConnectedAccountRequest;
import com.ctdecomerce.store.retailers.dto.IsRetailer;
import com.ctdecomerce.store.retailers.dto.UserIdRequest;
import com.ctdecomerce.store.retailers.model.RetailersModel;
import com.ctdecomerce.store.retailers.repository.RetailersRepo;
import com.ctdecomerce.store.user.model.UserModel;
import com.ctdecomerce.store.user.repository.UserRepo;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@Setter
@Getter
public class RetailersService {
    private final RetailersRepo retailersRepo;
    private final UserRepo userRepo;


    public RetailersService(RetailersRepo retailersRepo, UserRepo userRepo) {
        this.retailersRepo = retailersRepo;
        this.userRepo = userRepo;
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
        UserModel user = userRepo.findUserModelByUserId(userId);
        retailersModel.setUser(user);
        retailersRepo.save(retailersModel);
    }

    @Transactional
    public IsRetailer checkIfRetailer(UserIdRequest userIdRequest) {
        try {
            var user = userRepo.findUserModelByUserId(userIdRequest.getUserId());
            var retailer = retailersRepo.findRetailerByUser(user);
            return new IsRetailer(true);
        } catch (NoSuchElementException e) {
            return new IsRetailer(false);
        }
    }
}