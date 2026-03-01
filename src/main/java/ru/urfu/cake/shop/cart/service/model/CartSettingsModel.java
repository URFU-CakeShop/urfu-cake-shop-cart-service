package ru.urfu.cake.shop.cart.service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Модель настроек carts")
public class CartSettingsModel {

    @Schema(description = "Время жизни корзины")
    private Integer expirationHours;

    @Schema(description = "Максимальное количество карточек в корзине")
    private Integer maxCartCount;
}
