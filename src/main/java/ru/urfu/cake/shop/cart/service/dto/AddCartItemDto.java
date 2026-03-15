package ru.urfu.cake.shop.cart.service.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class AddCartItemDto {
    private UUID userId;             // Кому добавляем
    private UUID productVariantId;   // Готовый торт (может быть null)
    private UUID customCakeId;       // Кастомный торт (может быть null)
    private Integer quantity;        // Количество
}
