package ru.urfu.cake.shop.cart.service.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.cake.shop.cart.service.dto.AddCartItemDto;
import ru.urfu.cake.shop.cart.service.entity.CartItem;
import ru.urfu.cake.shop.cart.service.entity.Carts;
import ru.urfu.cake.shop.cart.service.exception.CartNotFoundException;
import ru.urfu.cake.shop.cart.service.exception.ItemNotFoundException;
import ru.urfu.cake.shop.cart.service.exception.NotYourCartException;
import ru.urfu.cake.shop.cart.service.repository.CartItemsRepository;
import ru.urfu.cake.shop.cart.service.repository.CartRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class CartsServiceImplTest {

    @Autowired
    private CartsServiceImpl cartService;

    @Autowired
    private CartSettingsService settingsService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemsRepository cartItemsRepository;

    @Test
    void getCartByUserId_shouldCreateNew_whenNotFound() {
        UUID userId = UUID.randomUUID();

        Carts cart = cartService.getCartByUserId(userId);

        assertThat(cart.getId()).isNotNull();
        assertThat(cart.getUserId()).isEqualTo(userId);
        assertThat(cart.getStatus()).isEqualTo("ACTIVE");
        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    void getCartByUserId_shouldReturnSame_whenExists() {
        UUID userId = UUID.randomUUID();

        Carts first = cartService.getCartByUserId(userId);
        Carts second = cartService.getCartByUserId(userId);

        assertThat(second.getId()).isEqualTo(first.getId());
    }

    @Test
    void addItemToCart_shouldCreateNewItem() {
        AddCartItemDto dto = new AddCartItemDto();
        dto.setUserId(UUID.randomUUID());
        dto.setProductVariantId(UUID.randomUUID());
        dto.setQuantity(3);

        Carts cart = cartService.addItemToCart(dto);

        assertThat(cart.getItems()).hasSize(1);
        CartItem item = cart.getItems().get(0);
        assertThat(item.getProductVariantId()).isEqualTo(dto.getProductVariantId());
        assertThat(item.getQuantity()).isEqualTo(3);
    }

    @Test
    void addItemToCart_shouldIncrement_whenSameProductExists() {
        UUID userId = UUID.randomUUID();
        UUID variantId = UUID.randomUUID();

        AddCartItemDto first = new AddCartItemDto();
        first.setUserId(userId);
        first.setProductVariantId(variantId);
        first.setQuantity(2);
        cartService.addItemToCart(first);

        AddCartItemDto second = new AddCartItemDto();
        second.setUserId(userId);
        second.setProductVariantId(variantId);
        second.setQuantity(4);
        Carts cart = cartService.addItemToCart(second);

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(6);
    }

    @Test
    void addItemToCart_shouldAddSeparateItem_whenDifferentProduct() {
        UUID userId = UUID.randomUUID();

        AddCartItemDto first = new AddCartItemDto();
        first.setUserId(userId);
        first.setProductVariantId(UUID.randomUUID());
        first.setQuantity(1);
        cartService.addItemToCart(first);

        AddCartItemDto second = new AddCartItemDto();
        second.setUserId(userId);
        second.setProductVariantId(UUID.randomUUID());
        second.setQuantity(2);
        Carts cart = cartService.addItemToCart(second);

        assertThat(cart.getItems()).hasSize(2);
    }

    @Test
    void addItemToCart_shouldExtendExpiration() {
        AddCartItemDto dto = new AddCartItemDto();
        dto.setUserId(UUID.randomUUID());
        dto.setProductVariantId(UUID.randomUUID());
        dto.setQuantity(1);

        Carts beforeAdd = cartService.getCartByUserId(dto.getUserId());
        var expiresBefore = beforeAdd.getExpiresAt();

        Carts afterAdd = cartService.addItemToCart(dto);

        assertThat(afterAdd.getExpiresAt()).isAfter(expiresBefore);
    }

    @Test
    void addItemToCart_shouldThrow_whenExceedsMaxItems() {
        settingsService.updateMaxCartCount(1);

        UUID userId = UUID.randomUUID();

        AddCartItemDto first = new AddCartItemDto();
        first.setUserId(userId);
        first.setProductVariantId(UUID.randomUUID());
        first.setQuantity(1);
        cartService.addItemToCart(first);

        AddCartItemDto second = new AddCartItemDto();
        second.setUserId(userId);
        second.setProductVariantId(UUID.randomUUID());
        second.setQuantity(1);

        assertThatThrownBy(() -> cartService.addItemToCart(second))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("лимит");
    }

    @Test
    void updateItemQuantity_shouldChangeValue() {
        AddCartItemDto dto = new AddCartItemDto();
        dto.setUserId(UUID.randomUUID());
        dto.setProductVariantId(UUID.randomUUID());
        dto.setQuantity(2);
        Carts cart = cartService.addItemToCart(dto);

        UUID itemId = cart.getItems().get(0).getId();
        Carts updated = cartService.updateItemQuantity(dto.getUserId(), itemId, 15);

        CartItem item = updated.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst().orElseThrow();
        assertThat(item.getQuantity()).isEqualTo(15);
    }

    @Test
    void updateItemQuantity_shouldThrow_whenNotOwner() {
        AddCartItemDto dto = new AddCartItemDto();
        dto.setUserId(UUID.randomUUID());
        dto.setProductVariantId(UUID.randomUUID());
        dto.setQuantity(1);
        Carts cart = cartService.addItemToCart(dto);

        UUID itemId = cart.getItems().get(0).getId();

        assertThatThrownBy(() -> cartService.updateItemQuantity(UUID.randomUUID(), itemId, 5))
                .isInstanceOf(NotYourCartException.class);
    }

    @Test
    void updateItemQuantity_shouldThrow_whenItemNotFound() {
        assertThatThrownBy(() -> cartService.updateItemQuantity(
                UUID.randomUUID(), UUID.randomUUID(), 5))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    void removeItemFromCart_shouldDeleteItem() {
        AddCartItemDto dto = new AddCartItemDto();
        dto.setUserId(UUID.randomUUID());
        dto.setProductVariantId(UUID.randomUUID());
        dto.setQuantity(1);
        Carts cart = cartService.addItemToCart(dto);

        UUID itemId = cart.getItems().get(0).getId();
        Carts updated = cartService.removeItemFromCart(dto.getUserId(), itemId);

        assertThat(updated.getItems()).isEmpty();
    }

    @Test
    void removeItemFromCart_shouldThrow_whenNotOwner() {
        AddCartItemDto dto = new AddCartItemDto();
        dto.setUserId(UUID.randomUUID());
        dto.setProductVariantId(UUID.randomUUID());
        dto.setQuantity(1);
        Carts cart = cartService.addItemToCart(dto);

        UUID itemId = cart.getItems().get(0).getId();

        assertThatThrownBy(() -> cartService.removeItemFromCart(UUID.randomUUID(), itemId))
                .isInstanceOf(NotYourCartException.class);
    }

    @Test
    void removeItemFromCart_shouldThrow_whenItemNotFound() {
        assertThatThrownBy(() -> cartService.removeItemFromCart(
                UUID.randomUUID(), UUID.randomUUID()))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    void clearCart_shouldRemoveAllItems() {
        UUID userId = UUID.randomUUID();

        AddCartItemDto first = new AddCartItemDto();
        first.setUserId(userId);
        first.setProductVariantId(UUID.randomUUID());
        first.setQuantity(2);
        cartService.addItemToCart(first);

        AddCartItemDto second = new AddCartItemDto();
        second.setUserId(userId);
        second.setProductVariantId(UUID.randomUUID());
        second.setQuantity(3);
        cartService.addItemToCart(second);

        cartService.clearCart(userId);

        Carts cart = cartService.getCartByUserId(userId);
        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    void clearCart_shouldThrow_whenCartNotFound() {
        assertThatThrownBy(() -> cartService.clearCart(UUID.randomUUID()))
                .isInstanceOf(CartNotFoundException.class);
    }

    @Test
    void addItemToCart_shouldNotPersistPriceInDatabase() {
        AddCartItemDto dto = new AddCartItemDto();
        dto.setUserId(UUID.randomUUID());
        dto.setProductVariantId(UUID.randomUUID());
        dto.setQuantity(2);
        Carts cart = cartService.addItemToCart(dto);

        CartItem saved = cartItemsRepository.findById(cart.getItems().get(0).getId()).orElseThrow();
        // price field was removed from entity — no setPrice call in service
        assertThat(saved.getQuantity()).isEqualTo(2);
        // verify no price-related fields exist via reflection
        assertThat(saved.getClass().getDeclaredFields())
                .noneMatch(f -> f.getName().equals("price"));
    }
}
