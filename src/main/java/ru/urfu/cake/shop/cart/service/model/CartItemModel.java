package ru.urfu.cake.shop.cart.service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@Schema(description = "Модель позиции в корзине")
public class CartItemModel {

    @Schema(description = "Уникальный идентификатор позиции")
    private UUID id;

    @Schema(description = "Идентификатор корзины, к которой относится товар")
    private UUID cartId;

    @Schema(description = "Идентификатор готового варианта торта из каталога")
    private UUID productVariantId;

    @Schema(description = "Идентификатор кастомного торта из конструктора")
    private UUID customCakeId;

    @Schema(description = "Количество товара в данной позиции")
    private Integer quantity;

    @Schema(description = "Цена за единицу товара на момент добавления")
    private BigDecimal price;

}