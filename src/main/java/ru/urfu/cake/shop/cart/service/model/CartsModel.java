package ru.urfu.cake.shop.cart.service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Schema(description = "Модель корзины пользователя")
public class CartsModel {

    @Schema(description = "Уникальный идентификатор корзины")
    private UUID id;

    @Schema(description = "Идентификатор владельца корзины")
    private UUID userId;

    @Schema(description = "Текущий статус корзины (ACTIVE, COMPLETED, ABANDONED)")
    private String status;

    @Schema(description = "Список товаров в корзине")
    private List<CartItemModel> items;

    @Schema(description = "Общая стоимость всей корзины")
    private BigDecimal totalAmount;

    @Schema(description = "Дата и время, когда корзина станет недействительной")
    private LocalDateTime expiresAt;

    @Schema(description = "Дата и время создания корзины")
    private LocalDateTime createdAt;
}