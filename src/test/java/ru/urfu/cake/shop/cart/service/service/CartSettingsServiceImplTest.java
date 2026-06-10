package ru.urfu.cake.shop.cart.service.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.cake.shop.cart.service.entity.CartSettings;
import ru.urfu.cake.shop.cart.service.exception.SettingsNotFoundException;
import ru.urfu.cake.shop.cart.service.model.CartSettingsModel;
import ru.urfu.cake.shop.cart.service.repository.CartSettingsRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class CartSettingsServiceImplTest {

    @Autowired
    private CartSettingsServiceImpl settingsService;

    @Autowired
    private CartSettingsRepository repository;

    @Test
    void getSettings_shouldReturnExisting() {
        CartSettings settings = settingsService.getSettings();

        assertThat(settings).isNotNull();
        assertThat(settings.getId()).isEqualTo(1);
        assertThat(settings.getExpirationHours()).isPositive();
    }

    @Test
    void getSettingsModel_shouldMapFields() {
        CartSettingsModel model = settingsService.getSettingsModel();

        assertThat(model).isNotNull();
        assertThat(model.getExpirationHours()).isEqualTo(24);
        assertThat(model.getMaxCartCount()).isEqualTo(50);
    }

    @Test
    void updateExpiration_shouldPersist() {
        settingsService.updateExpiration(72);

        CartSettings settings = repository.findById(1).orElseThrow();
        assertThat(settings.getExpirationHours()).isEqualTo(72);
    }

    @Test
    void updateExpiration_shouldThrowOnNegative() {
        assertThatThrownBy(() -> settingsService.updateExpiration(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("отрицательным");
    }

    @Test
    void updateMaxCartCount_shouldPersist() {
        settingsService.updateMaxCartCount(25);

        CartSettings settings = repository.findById(1).orElseThrow();
        assertThat(settings.getMaxItemsCount()).isEqualTo(25);
    }

    @Test
    void updateMaxCartCount_shouldThrowOnNegative() {
        assertThatThrownBy(() -> settingsService.updateMaxCartCount(-10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("отрицательным");
    }
}
