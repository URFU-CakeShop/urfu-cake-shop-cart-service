package ru.urfu.cake.shop.cart.service.service;

import ru.urfu.cake.shop.cart.service.entity.CartSettings;
import ru.urfu.cake.shop.cart.service.model.CartSettingsModel;

public interface CartSettingsService {


    /**
     * Получить сущность настроек напрямую из БД.
     * Используется для внутренней логики других сервисов.
     */
    CartSettings getSettings();

    /**
     * Получить модель настроек для отображения.
     */
    CartSettingsModel getSettingsModel();

    /**
     * Обновить время жизни корзины (в часах).
     * * @param hours количество часов до истечения срока действия корзины.
     * @throws IllegalArgumentException если передано отрицательное число.
     */
    void updateExpiration(int hours);

    /**
     * Обновить лимит на количество уникальных позиций в одной корзине.
     * * @param maxCartCount максимально допустимое количество предметов.
     * @throws IllegalArgumentException если передано отрицательное число.
     */
    void updateMaxCartCount(int maxCartCount);
}
