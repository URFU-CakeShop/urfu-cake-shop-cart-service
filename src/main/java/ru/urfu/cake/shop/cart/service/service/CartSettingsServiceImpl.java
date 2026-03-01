package ru.urfu.cake.shop.cart.service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.urfu.cake.shop.cart.service.entity.CartSettings;
import ru.urfu.cake.shop.cart.service.exception.SettingsNotFoundException;
import ru.urfu.cake.shop.cart.service.model.CartSettingsModel;
import ru.urfu.cake.shop.cart.service.repository.CartSettingsRepository;

@Service
@RequiredArgsConstructor
public class CartSettingsServiceImpl implements CartSettingsService {

    private final CartSettingsRepository repository;
    private static final Integer SETTINGS_ID = 1;

    @Override
    public CartSettings getSettings() {
        return repository.findById(SETTINGS_ID)
                .orElseThrow(() -> new SettingsNotFoundException());
    }

    @Override
    public CartSettingsModel getSettingsModel() {
        return toModel(getSettings());
    }

    @Override
    @Transactional
    public void updateExpiration(int hours) {
        if (hours < 0) {
            throw new IllegalArgumentException("Время жизни корзины не может быть отрицательным");
        }
        CartSettings settings = getSettings();
        settings.setExpirationHours(hours);
        repository.save(settings);
    }

    @Override
    @Transactional
    public void updateMaxCartCount(int maxCartCount) {
        if (maxCartCount < 0) {
            throw new IllegalArgumentException("Количество предметов не может быть отрицательным");
        }
        CartSettings settings = getSettings();
        settings.setMaxItemsCount(maxCartCount);
        repository.save(settings);
    }

    private CartSettingsModel toModel(CartSettings settings) {
        return CartSettingsModel.builder()
                .expirationHours(settings.getExpirationHours())
                .maxCartCount(settings.getMaxItemsCount())
                .build();
    }
}
