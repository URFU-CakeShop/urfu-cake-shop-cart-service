package ru.urfu.cake.shop.cart.service.controller;


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

    @GetMapping
    public ResponseEntity<ApiResponse<CartSettingsModel>> getSettings() {
        return ResponseEntity.ok(new ApiResponse<>(true, settingsService.getSettingsModel(), "Настройки получены"));
    }

    @PatchMapping("/expiration")
    public ResponseEntity<ApiResponse<Void>> updateExpiration(@RequestParam int hours) {
        settingsService.updateExpiration(hours);
        return ResponseEntity.ok(new ApiResponse<>(true, null, "Время жизни обновлено"));
    }

    @PatchMapping("/max-count")
    public ResponseEntity<ApiResponse<Void>> updateMaxCount(@RequestParam int count) {
        settingsService.updateMaxCartCount(count);
        return ResponseEntity.ok(new ApiResponse<>(true, null, "Лимит предметов обновлен"));
    }
}
