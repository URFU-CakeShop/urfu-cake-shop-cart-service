package ru.urfu.cake.shop.cart.service.exception;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException() {
        super("Позиция в корзине не найдена");
    }
}
