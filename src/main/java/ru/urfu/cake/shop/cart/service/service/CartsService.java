package ru.urfu.cake.shop.cart.service.service;

import ru.urfu.cake.shop.cart.service.dto.AddCartItemDto;
import ru.urfu.cake.shop.cart.service.entity.Carts;
import ru.urfu.cake.shop.cart.service.model.CartsModel;

import java.util.UUID;

public interface CartsService {


    /**
     * Получить корзину
     */
    Carts getCartByUserId(UUID userId);

    /**
     * Добавить позицию в корзину
     */
    Carts addItemToCart(AddCartItemDto dto);

    /**
     * Изменить количество конкретной позиции
     */
    Carts updateItemQuantity(UUID userId, UUID itemId, Integer quantity);

    /**
     * Удалить конкретную позицию из корзины
     */
    Carts removeItemFromCart(UUID userId, UUID itemId);

    /**
     * Полностью очистить корзину пользователя
     */
    void clearCart(UUID userId);

}