package ru.urfu.cake.shop.cart.service.exception;

public class NotYourCartException extends RuntimeException {
    public NotYourCartException() {
        super("У вас нет прав для модификации этой позиции");
    }
}
