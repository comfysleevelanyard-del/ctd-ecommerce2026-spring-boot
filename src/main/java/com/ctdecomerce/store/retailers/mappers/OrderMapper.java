package com.ctdecomerce.store.retailers.mappers;


import com.ctdecomerce.store.delivery.model.DeliveryModel;
import com.ctdecomerce.store.orders.model.OrdersModel;
import com.ctdecomerce.store.retailers.dto.OrderItemDto;
import com.ctdecomerce.store.retailers.dto.RetailerOrdersDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", nullValueCheckStrategy = org.mapstruct.NullValueCheckStrategy.ALWAYS)
public interface OrderMapper {
    @Mapping(source = "cart.product.id", target = "productId")
    @Mapping(source = "cart.product.name", target = "productName")
    @Mapping(source = "cart.quantity", target = "quantity")
    @Mapping(source = "cart.product.priceInCents", target = "price")
    @Mapping(source = "cart.user.name", target = "userName")
    @Mapping(source = "cart.user.email", target = "email")

    //@Mapping(source = "completed", target = "COMPLETED")
    //@Mapping(source = "cart.user.email", target = "email")
//    @Mapping(source = "cart.product.showing", target = "isShowing")
//    @Mapping(source = "cart.product.available", target = "isAvailable")

    OrderItemDto toDto(OrdersModel ordersModel);
}
