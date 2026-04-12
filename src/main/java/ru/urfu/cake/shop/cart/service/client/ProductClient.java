package ru.urfu.cake.shop.cart.service.client;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.urfu.cake.shop.cart.service.client.Response.BaseProductResponse;
import ru.urfu.cake.shop.cart.service.client.dto.ProductPriceDto;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductClient {

    private final WebClient productServiceWebClient;

    public BigDecimal getPrice(UUID productVariantId) {
        try {
            return productServiceWebClient.get()
                    .uri("/product/variant/{id}", productVariantId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<BaseProductResponse<ProductPriceDto>>() {})
                    .map(response -> {
                        if (response != null && response.isSuccess() && response.getData() != null) {
                            return response.getData().getPrice();
                        }
                        log.warn("Сервис продуктов вернул неуспешный ответ или пустые данные для ID: {}", productVariantId);
                        return BigDecimal.ZERO;
                    })
                    .defaultIfEmpty(BigDecimal.ZERO)
                    .block();
        } catch (Exception e) {
            log.error("Ошибка при обращении к Catalog Service для ID {}: {}", productVariantId, e.getMessage());
            return BigDecimal.ZERO;
        }
    }
}
