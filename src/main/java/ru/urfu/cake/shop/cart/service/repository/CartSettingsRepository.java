package ru.urfu.cake.shop.cart.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.cake.shop.cart.service.entity.CartSettings;

public interface CartSettingsRepository extends JpaRepository<CartSettings, Integer> {
}
