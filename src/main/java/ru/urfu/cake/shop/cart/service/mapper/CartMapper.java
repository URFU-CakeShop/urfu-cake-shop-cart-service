package ru.urfu.cake.shop.cart.service.mapper;

import org.springframework.stereotype.Component;
import ru.urfu.cake.shop.cart.service.entity.CartItem;
import ru.urfu.cake.shop.cart.service.entity.Carts;
import ru.urfu.cake.shop.cart.service.model.CartItemModel;
import ru.urfu.cake.shop.cart.service.model.CartsModel;

import java.math.BigDecimal;

@Component
public class CartMapper {
    public CartsModel toModel(Carts cart) {
        BigDecimal total = cart.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartsModel.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .status(cart.getStatus())
                .expiresAt(cart.getExpiresAt())
                .createdAt(cart.getCreatedAt())
                .totalAmount(total)
                .items(cart.getItems().stream().map(this::toItemModel).toList())
                .build();
    }

    public CartItemModel toItemModel(CartItem item) {
        return CartItemModel.builder()
                .id(item.getId())
                .cartId(item.getCart().getId())
                .productVariantId(item.getProductVariantId())
                .customCakeId(item.getCustomCakeId())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build();
    }
}
