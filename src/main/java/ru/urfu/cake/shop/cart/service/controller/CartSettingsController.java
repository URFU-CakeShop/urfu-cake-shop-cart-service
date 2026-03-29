package ru.urfu.cake.shop.cart.service.controller;


import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.urfu.cake.shop.cart.service.dto.ApiResponse;
import ru.urfu.cake.shop.cart.service.entity.CartSettings;
import ru.urfu.cake.shop.cart.service.model.CartSettingsModel;
import ru.urfu.cake.shop.cart.service.service.CartSettingsService;
import ru.urfu.cake.shop.cart.service.service.CartsService;

@RestController
@RequestMapping(value = "/api/carts/settings")
@RequiredArgsConstructor
public class CartSettingsController {

    private final CartSettingsService settingsService;

    @Operation(summary = "Получить все дополнительные настройки",
            description = "На данный момент есть толко 2 настройки: максимальное колличество карт в карзине и время жизни самой карзины в часах")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Успешный успех")
    })
    @Timed
    @GetMapping
    public ResponseEntity<ApiResponse<CartSettingsModel>> getSettings() {
        return ResponseEntity.ok(new ApiResponse<>(true, settingsService.getSettingsModel(), "Настройки получены"));
    }
    @Operation(summary = "Обновить время жизни карзины",
            description = "Обновляет время жизни карзины, принимает число int и засчитывает его за часы.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Успешный успех")
    })
    @Timed
    @PatchMapping("/expiration")
    public ResponseEntity<ApiResponse<Void>> updateExpiration(@RequestParam int hours) {
        settingsService.updateExpiration(hours);
        return ResponseEntity.ok(new ApiResponse<>(true, null, "Время жизни обновлено"));
    }

    @Operation(summary = "Обновить лимит предметов в карзине",
            description = "Принимает число int.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Успешный успех")
    })
    @Timed
    @PatchMapping("/max-count")
    public ResponseEntity<ApiResponse<Void>> updateMaxCount(@RequestParam int count) {
        settingsService.updateMaxCartCount(count);
        return ResponseEntity.ok(new ApiResponse<>(true, null, "Лимит предметов обновлен"));
    }
}
