package com.ctdecomerce.store.delivery.service;

import com.ctdecomerce.store.delivery.dto.CreateDeliveryDTO;
import com.ctdecomerce.store.delivery.dto.OrderIdReqeust;
import com.ctdecomerce.store.delivery.model.DeliveryModel;
import com.ctdecomerce.store.delivery.repository.DeliveryRepo;
import com.ctdecomerce.store.discounts.model.DiscountsModel;
import com.ctdecomerce.store.discounts.repository.DiscountsRepo;
import com.ctdecomerce.store.orders.model.OrdersModel;
import com.ctdecomerce.store.orders.repository.OrdersRepo;
import com.ctdecomerce.store.retailers.model.RetailersModel;
import com.ctdecomerce.store.retailers.repository.RetailersRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Setter
@Service
public class DeliveryService {
    private final DeliveryRepo deliveryRepo;
    private final RetailersRepo retailersRepo;
    private final OrdersRepo ordersRepo;
    private final DiscountsRepo discountsRepo;

    @Transactional
    public void createNewDelivery(CreateDeliveryDTO createDeliveryDTO) {
        RetailersModel retailer = retailersRepo.findById(createDeliveryDTO.getRetailerId()).orElse(null);
        OrdersModel order = ordersRepo.findById(createDeliveryDTO.getOrderId()).orElse(null);
        DeliveryModel delivery = new DeliveryModel();
        delivery.setRetailer(retailer);
        delivery.setOrder(order);
        delivery.setShippingAddress("");
        deliveryRepo.save(delivery);
    }

    @Transactional
    public DeliveryModel getDeliveryByOrderId(OrderIdReqeust orderIdReqeust) {
        UUID orderIdUUID = UUID.fromString(orderIdReqeust.getOrderId());
        OrdersModel order = ordersRepo.findById(orderIdUUID).orElse(null);
        return deliveryRepo.getDeliveryModelByOrder(order);
    }
}
