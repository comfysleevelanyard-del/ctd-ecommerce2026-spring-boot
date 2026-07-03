package com.ctdecomerce.store.retailers.controller;

import com.ctdecomerce.store.dto.IdRequest;
import com.ctdecomerce.store.product.model.ProductModel;
import com.ctdecomerce.store.retailers.dto.ConnectedAccountDTO;
import com.ctdecomerce.store.retailers.dto.ConnectedAccountRequest;
import com.ctdecomerce.store.retailers.service.RetailersService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController("RetailersController")
@RequestMapping("/retailers")
public class RetailersController {
    private final RetailersService retailersService;
    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    public RetailersController(RetailersService retailersService) {
        this.retailersService = retailersService;
    }

    @PostMapping("/create")
    public ResponseEntity<ConnectedAccountDTO> createAccount(@RequestBody ConnectedAccountRequest connectedAccountRequest) throws StripeException {
        return new ResponseEntity<>(retailersService.createNewRetailer(connectedAccountRequest), HttpStatus.CREATED);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> retailersWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) throws StripeException {
        if (sigHeader == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD REQUEST MISSING WEBHOOK SIGNITURE");
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Signature verification failed");
        }
        if ("account.updated".equals(event.getType())) {
            Account account = Account.retrieve(event.getAccount());
            System.out.println("Running");
            if (account.getChargesEnabled()) {
                System.out.println("Running");
                Map<String, String> metadata = account.getMetadata();
                retailersService.createAccountToDB(metadata.get("name"), account.getId(), metadata.get("userId"));
                return ResponseEntity.status(HttpStatus.OK).body("Success");
            }
        }


        return ResponseEntity.status(HttpStatus.OK).

                body("No event found");
    }
}

