package ru.urfu.cake.shop.cart.service.exception;

import java.util.UUID;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(UUID userId) {
        super("Корзина для пользователя с ID " + userId + " не найдена");
    }
}