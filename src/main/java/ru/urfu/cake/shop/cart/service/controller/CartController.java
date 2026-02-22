package ru.urfu.cake.shop.cart.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.urfu.cake.shop.cart.service.dto.AddCartItemDto;
import ru.urfu.cake.shop.cart.service.dto.ApiResponse;
import ru.urfu.cake.shop.cart.service.dto.UpdateQuantityDto;
import ru.urfu.cake.shop.cart.service.entity.Carts;
import ru.urfu.cake.shop.cart.service.model.CartsModel;
import ru.urfu.cake.shop.cart.service.service.CartsServiceImpl;

import java.util.UUID;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartsServiceImpl cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<CartsModel>> getCartByUserId(@PathVariable UUID userId) {
        Carts cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, cartService.toModel(cart), "Корзина получена"));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartsModel>> addItemToCart(@RequestBody AddCartItemDto request) {
        Carts cart = cartService.addItemToCart(request);
        return ResponseEntity.ok(new ApiResponse<>(true, cartService.toModel(cart), "Товар добавлен"));
    }

    @PatchMapping("/{userId}/items/{itemId}")
    public ResponseEntity<ApiResponse<CartsModel>> updateItemQuantity(
            @PathVariable UUID userId,
            @PathVariable UUID itemId,
            @RequestBody UpdateQuantityDto request) {
        Carts cart = cartService.updateItemQuantity(userId, itemId, request.getQuantity());
        return ResponseEntity.ok(new ApiResponse<>(true, cartService.toModel(cart), "Количество обновлено"));
    }

    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<ApiResponse<CartsModel>> removeItem(
            @PathVariable UUID userId,
            @PathVariable UUID itemId) {
        Carts cart = cartService.removeItemFromCart(userId, itemId);
        return ResponseEntity.ok(new ApiResponse<>(true, cartService.toModel(cart), "Товар удален"));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> clearCart(@PathVariable UUID userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, null, "Корзина полностью очищена"));
    }
}