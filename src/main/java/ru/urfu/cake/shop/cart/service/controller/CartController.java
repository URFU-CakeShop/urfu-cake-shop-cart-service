package ru.urfu.cake.shop.cart.service.controller;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.urfu.cake.shop.cart.service.dto.AddCartItemDto;
import ru.urfu.cake.shop.cart.service.dto.UpdateQuantityDto;
import ru.urfu.cake.shop.cart.service.entity.Carts;
import ru.urfu.cake.shop.cart.service.mapper.CartMapper;
import ru.urfu.cake.shop.cart.service.model.CartsModel;
import ru.urfu.cake.shop.cart.service.service.CartsService;
import ru.urfu.cake.shop.cart.service.service.CartsServiceImpl;

import java.util.UUID;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Tag(name = "Корзина", description = "Корзина покупок пользователей")
public class CartController {

    private final CartsService cartService;
    private final CartMapper cartMapper;



    @Operation(summary = "Получить корзину по ID пользователя",
            description = "Запрашивает текущую активную корзину. Если корзины нет, создаст новую")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Корзина успешно получена"),
            @ApiResponse(responseCode = "404", description = "Корзина не найдена",
                    content = @Content(schema = @Schema(implementation = ru.urfu.cake.shop.cart.service.dto.ApiResponse.class)))
    })
    @Timed
    @GetMapping("/{userId}")
    public ResponseEntity<ru.urfu.cake.shop.cart.service.dto.ApiResponse<CartsModel>> getCartByUserId(@PathVariable UUID userId) {
        Carts cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(new ru.urfu.cake.shop.cart.service.dto.ApiResponse<>(true, cartMapper.toModel(cart), "Корзина получена"));
    }


    @Operation(summary = "Добавить позицию в корзину",
            description = "Добавляет товар (вариант торта или кастомный торт). Если такой товар уже есть в корзине, его количество увеличится.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Товар добавлен/обновлен"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные (например, отрицательное количество)",
                    content = @Content(schema = @Schema(implementation = ru.urfu.cake.shop.cart.service.dto.ApiResponse.class)))
    })
    @Timed
    @PostMapping("/items")
    public ResponseEntity<ru.urfu.cake.shop.cart.service.dto.ApiResponse<CartsModel>> addItemToCart(@RequestBody AddCartItemDto request) {
        Carts cart = cartService.addItemToCart(request);
        return ResponseEntity.ok(new ru.urfu.cake.shop.cart.service.dto.ApiResponse<>(true, cartMapper.toModel(cart), "Товар добавлен"));
    }


    @Operation(summary = "Обновить количество товара",
            description = "Изменяет количество конкретной позиции в корзине на указанное значение.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Количество успешно изменено"),
            @ApiResponse(responseCode = "400", description = "Ошибка доступа (не своя корзина) или неверный формат данных",
                    content = @Content(schema = @Schema(implementation = ru.urfu.cake.shop.cart.service.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "item или корзина не найдена",
                    content = @Content(schema = @Schema(implementation = ru.urfu.cake.shop.cart.service.dto.ApiResponse.class)))
    })
    @Timed
    @PatchMapping("/{userId}/items/{itemId}")
    public ResponseEntity<ru.urfu.cake.shop.cart.service.dto.ApiResponse<CartsModel>> updateItemQuantity(
        @Parameter(description = "ID пользователя") @PathVariable UUID userId,
        @Parameter(description = "ID позиции внутри корзины") @PathVariable UUID itemId,
        @RequestBody UpdateQuantityDto request) {
        Carts cart = cartService.updateItemQuantity(userId, itemId, request.getQuantity());
        return ResponseEntity.ok(new ru.urfu.cake.shop.cart.service.dto.ApiResponse<>(true, cartMapper.toModel(cart), "Количество обновлено"));
    }

    @Operation(summary = "Удалить товар из корзины")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Товар удален"),
            @ApiResponse(responseCode = "404", description = "Товар не найден в корзине")
    })
    @Timed
    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<ru.urfu.cake.shop.cart.service.dto.ApiResponse<CartsModel>> removeItem(
            @PathVariable UUID userId,
            @PathVariable UUID itemId) {
        Carts cart = cartService.removeItemFromCart(userId, itemId);
        return ResponseEntity.ok(new ru.urfu.cake.shop.cart.service.dto.ApiResponse<>(true, cartMapper.toModel(cart), "Товар удален"));
    }

    @Operation(summary = "Очистить корзину целиком", description = "Удаляет все товары из корзины пользователя.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Корзина очищена"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @Timed
    @DeleteMapping("/{userId}")
    public ResponseEntity<ru.urfu.cake.shop.cart.service.dto.ApiResponse<Void>> clearCart(@PathVariable UUID userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(new ru.urfu.cake.shop.cart.service.dto.ApiResponse<>(true, null, "Корзина полностью очищена"));
    }
}