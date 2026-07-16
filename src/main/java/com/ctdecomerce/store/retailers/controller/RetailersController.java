package com.ctdecomerce.store.retailers.controller;

import com.ctdecomerce.store.cart.model.CartModel;
import com.ctdecomerce.store.cart.repo.CartRepo;
import com.ctdecomerce.store.delivery.dto.CreateDeliveryDTO;
import com.ctdecomerce.store.delivery.service.DeliveryService;
import com.ctdecomerce.store.discounts.model.DiscountsModel;
import com.ctdecomerce.store.discounts.repository.DiscountsRepo;
import com.ctdecomerce.store.orders.model.OrdersModel;
import com.ctdecomerce.store.orders.repository.OrdersRepo;
import com.ctdecomerce.store.product.model.ProductModel;
import com.ctdecomerce.store.product.repository.ProductRepo;
import com.ctdecomerce.store.retailers.dto.ConnectedAccountDTO;
import com.ctdecomerce.store.retailers.dto.ConnectedAccountRequest;
import com.ctdecomerce.store.retailers.service.RetailersService;
import com.ctdecomerce.store.user.model.UserModel;
import com.ctdecomerce.store.user.repository.UserRepo;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.Transfer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.TransferCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/retailers")
public class RetailersController {

    private final RetailersService retailersService;
    private final CartRepo cartRepo;
    private final UserRepo userRepo;
    private final OrdersRepo ordersRepo;
    private final DeliveryService deliveryService;
    private final DiscountsRepo discountsRepo;
    private final ProductRepo productRepo;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    public RetailersController(
            RetailersService retailersService,
            CartRepo cartRepo,
            UserRepo userRepo,
            OrdersRepo ordersRepo,
            DeliveryService deliveryService,
            DiscountsRepo discountsRepo,
            ProductRepo productRepo
    ) {
        this.retailersService = retailersService;
        this.cartRepo = cartRepo;
        this.userRepo = userRepo;
        this.ordersRepo = ordersRepo;
        this.deliveryService = deliveryService;
        this.discountsRepo = discountsRepo;
        this.productRepo = productRepo;
    }

    @PostMapping("/create")
    public ResponseEntity<ConnectedAccountDTO> createAccount(
            @RequestBody ConnectedAccountRequest request
    ) throws StripeException {

        ConnectedAccountDTO account =
                retailersService.createNewRetailer(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(account);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> retailersWebhook(
            @RequestBody String payload,
            @RequestHeader(
                    value = "Stripe-Signature",
                    required = false
            ) String signature
    ) {
        if (signature == null || signature.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("Missing Stripe signature");
        }

        Event event;

        try {
            event = Webhook.constructEvent(
                    payload,
                    signature,
                    webhookSecret
            );
        } catch (SignatureVerificationException e) {
            return ResponseEntity
                    .badRequest()
                    .body("Invalid Stripe signature");
        }

        System.out.println("Stripe event received: " + event.getType());

        try {
            return switch (event.getType()) {
                case "account.updated" ->
                        handleAccountUpdated(event);

                case "checkout.session.completed" ->
                        handleCheckoutCompleted(event);

                default -> ResponseEntity.ok(
                        "Event ignored: " + event.getType()
                );
            };
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Webhook processing failed");
        }
    }

    private ResponseEntity<String> handleAccountUpdated(Event event) {
        EventDataObjectDeserializer deserializer =
                event.getDataObjectDeserializer();

        StripeObject stripeObject = deserializer
                .getObject()
                .orElse(null);

        if (!(stripeObject instanceof Account account)) {
            return ResponseEntity
                    .badRequest()
                    .body("Unable to deserialize Stripe account");
        }

        System.out.println("Account ID: " + account.getId());
        System.out.println(
                "Charges enabled: " + account.getChargesEnabled()
        );
        System.out.println(
                "Payouts enabled: " + account.getPayoutsEnabled()
        );
        System.out.println(
                "Details submitted: " + account.getDetailsSubmitted()
        );

        boolean chargesEnabled =
                Boolean.TRUE.equals(account.getChargesEnabled());

        boolean payoutsEnabled =
                Boolean.TRUE.equals(account.getPayoutsEnabled());

        if (!chargesEnabled || !payoutsEnabled) {
            return ResponseEntity.ok(
                    "Retailer onboarding is not complete"
            );
        }

        Map<String, String> metadata = account.getMetadata();

        if (metadata == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Stripe account metadata is missing");
        }

        String name = metadata.get("name");
        String userId = metadata.get("userId");

        if (name == null || name.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("Retailer name is missing");
        }

        if (userId == null || userId.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("Retailer user ID is missing");
        }

        retailersService.createAccountToDB(
                name,
                account.getId(),
                userId
        );

        return ResponseEntity.ok(
                "Retailer account saved successfully"
        );
    }

    private ResponseEntity<String> handleCheckoutCompleted(
            Event event
    ) throws StripeException {

        StripeObject stripeObject = event
                .getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        if (!(stripeObject instanceof Session session)) {
            return ResponseEntity
                    .badRequest()
                    .body("Unable to deserialize checkout session");
        }

        String paymentIntentId = session.getPaymentIntent();

        if (paymentIntentId == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Payment intent is missing");
        }

        PaymentIntent paymentIntent =
                PaymentIntent.retrieve(paymentIntentId);

        String chargeId = paymentIntent.getLatestCharge();

        if (chargeId == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Charge ID is missing");
        }

        Map<String, String> metadata = session.getMetadata();

        if (metadata == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Checkout metadata is missing");
        }

        String userId = metadata.get("userId");

        if (userId == null || userId.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("User ID is missing");
        }

        UserModel user =
                userRepo.findUserModelByUserId(userId);

        if (user == null) {
            return ResponseEntity
                    .badRequest()
                    .body("User was not found");
        }

        List<CartModel> carts =
                cartRepo.findCartModelsByUserId(user, true);

        for (CartModel cart : carts) {
            processCartItem(cart, user, chargeId);
        }

        return ResponseEntity.ok(
                "Checkout completed successfully"
        );
    }

    private void processCartItem(
            CartModel cart,
            UserModel user,
            String chargeId
    ) throws StripeException {

        cart.setShowing(false);
        cartRepo.save(cart);

        ProductModel product = cart.getProduct();

        if (product == null) {
            throw new IllegalStateException(
                    "Cart product is missing"
            );
        }

        if (product.getStock() < cart.getQuantity()) {
            throw new IllegalStateException(
                    "Not enough product stock"
            );
        }

        product.setStock(
                product.getStock() - cart.getQuantity()
        );

        productRepo.save(product);

        double finalPrice = calculateFinalPrice(cart);

        OrdersModel order = new OrdersModel();
        order.setCart(cart);
        order.setUser(user);
        order.setFinalPriceInCents(finalPrice);

        OrdersModel savedOrder = ordersRepo.save(order);

        if (product.getOwner() == null) {
            throw new IllegalStateException(
                    "Product retailer is missing"
            );
        }

        deliveryService.createNewDelivery(
                new CreateDeliveryDTO(
                        savedOrder.getId(),
                        product.getOwner().getId()
                )
        );

        createRetailerTransfer(
                product,
                finalPrice,
                chargeId
        );
    }

    private double calculateFinalPrice(CartModel cart) {
        ProductModel product = cart.getProduct();

        DiscountsModel discount =
                discountsRepo.findDiscountsModelByProduct(product);

        double unitPrice = product.getPriceInCents();

        if (discount != null) {
            unitPrice = unitPrice -
                    (unitPrice * discount.getOffer());
        }

        return unitPrice * cart.getQuantity();
    }

    private void createRetailerTransfer(
            ProductModel product,
            double finalPrice,
            String chargeId
    ) throws StripeException {

        String destinationAccountId =
                product.getOwner().getAccountId();

        if (destinationAccountId == null
                || destinationAccountId.isBlank()) {
            throw new IllegalStateException(
                    "Retailer Stripe account ID is missing"
            );
        }

        long retailerAmount =
                Math.round(finalPrice * 0.87);

        TransferCreateParams transferParams =
                TransferCreateParams.builder()
                        .setAmount(retailerAmount)
                        .setCurrency("usd")
                        .setDestination(destinationAccountId)
                        .setSourceTransaction(chargeId)
                        .setDescription(
                                "Transfer to merchant account"
                        )
                        .build();

        Transfer.create(transferParams);
    }
}