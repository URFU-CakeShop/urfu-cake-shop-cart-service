package ru.urfu.cake.shop.cart.service.client.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;


@Data
public class ProductPriceDto {
    private UUID id;
    private UUID productId;
    private String sku;
    private BigDecimal price;
    private String currency;
    private Integer weight;
    private Boolean active;
}
