package ru.urfu.cake.shop.cart.service.exception;

public class SettingsNotFoundException extends RuntimeException {
    public SettingsNotFoundException() {
        super("Системные настройки корзины не инициализированы в БД");
    }
}
