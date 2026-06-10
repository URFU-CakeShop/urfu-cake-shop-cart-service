package ru.urfu.cake.shop.cart.service.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.urfu.cake.shop.cart.service.client.ProductClient;
import ru.urfu.cake.shop.cart.service.entity.CartItem;
import ru.urfu.cake.shop.cart.service.entity.Carts;
import ru.urfu.cake.shop.cart.service.model.CartItemModel;
import ru.urfu.cake.shop.cart.service.model.CartsModel;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CartMapper {

    private final ProductClient productClient;

    public CartsModel toModel(Carts cart) {
        List<CartItemModel> itemModels = cart.getItems().stream()
                .map(this::toItemModel)
                .toList();

        BigDecimal total = itemModels.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartsModel.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .status(cart.getStatus())
                .expiresAt(cart.getExpiresAt())
                .createdAt(cart.getCreatedAt())
                .totalAmount(total)
                .items(itemModels)
                .build();
    }

    public CartItemModel toItemModel(CartItem item) {
        BigDecimal price = item.getProductVariantId() != null
                ? productClient.getPrice(item.getProductVariantId())
                : BigDecimal.ZERO;

        return CartItemModel.builder()
                .id(item.getId())
                .cartId(item.getCart().getId())
                .productVariantId(item.getProductVariantId())
                .customCakeId(item.getCustomCakeId())
                .quantity(item.getQuantity())
                .price(price)
                .build();
    }
}
