package ru.urfu.cake.shop.cart.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.cake.shop.cart.service.dto.AddCartItemDto;
import ru.urfu.cake.shop.cart.service.entity.CartItem;
import ru.urfu.cake.shop.cart.service.entity.CartSettings;
import ru.urfu.cake.shop.cart.service.entity.Carts;
import ru.urfu.cake.shop.cart.service.exception.CartNotFoundException;
import ru.urfu.cake.shop.cart.service.exception.ItemNotFoundException;
import ru.urfu.cake.shop.cart.service.exception.NotYourCartException;
import ru.urfu.cake.shop.cart.service.repository.CartItemsRepository;
import ru.urfu.cake.shop.cart.service.repository.CartRepository;
import ru.urfu.cake.shop.cart.service.service.metrics.CartCounterService;
import ru.urfu.cake.shop.cart.service.util.TimeUtil;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartsServiceImpl implements CartsService {

    private final CartRepository cartRepository;
    private final CartItemsRepository cartItemsRepository;
    private final CartSettingsService settingsService;
    private final CartCounterService cartCounterService;


    @Override
    public Carts getCartByUserId(UUID userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(userId));
    }

    @Override
    @Transactional
    public Carts addItemToCart(AddCartItemDto dto) {
        Carts cart = getCartByUserId(dto.getUserId());
        CartSettings settings = settingsService.getSettings();

        // Устанавливаем динамическое время жизни из настроек
        cart.setExpiresAt(TimeUtil.now().plusHours(settings.getExpirationHours()));

        // Ищем, есть ли товар
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> isSameProduct(item, dto.getProductVariantId(), dto.getCustomCakeId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + dto.getQuantity());
        } else {
            // если добавляем новую позицию
            if (cart.getItems().size() >= settings.getMaxItemsCount()) {
                throw new IllegalArgumentException("Достигнут лимит позиций в корзине: " + settings.getMaxItemsCount());
            }

            CartItem newItem = new CartItem();
            UUID productVariantId = dto.getProductVariantId();
            newItem.setCart(cart);
            newItem.setProductVariantId(productVariantId);
            newItem.setCustomCakeId(dto.getCustomCakeId());
            newItem.setQuantity(dto.getQuantity());

            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Carts updateItemQuantity(UUID userId, UUID itemId, Integer quantity) {
        CartItem item = cartItemsRepository.findById(itemId)
                .orElseThrow(ItemNotFoundException::new);

        // Проверка безопасности: совпадает ли владелец корзины с пришедшим userId
        if (!item.getCart().getUserId().equals(userId)) {
            throw new NotYourCartException();
        }

        item.setQuantity(quantity);
        cartItemsRepository.save(item);

        return item.getCart();
    }

    @Override
    @Transactional
    public Carts removeItemFromCart(UUID userId, UUID itemId) {
        CartItem item = cartItemsRepository.findById(itemId)
                .orElseThrow(ItemNotFoundException::new);

        // Проверка безопасности
        if (!item.getCart().getUserId().equals(userId)) {
            throw new NotYourCartException();
        }

        Carts cart = item.getCart();
        cart.getItems().remove(item);
        cartItemsRepository.delete(item);

        return cart;
    }

    @Override
    @Transactional
    public void clearCart(UUID userId) {
        Carts cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));

        cartItemsRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cartRepository.save(cart);
        cartCounterService.decrement();
    }

    // --- Вспомогательные методы ---

    private Carts createNewCart(UUID userId) {
        Carts newCart = new Carts();
        newCart.setUserId(userId);
        newCart.setStatus("ACTIVE");
        newCart.setCreatedAt(TimeUtil.now());

        Integer hours = settingsService.getSettings().getExpirationHours();
        newCart.setExpiresAt(TimeUtil.now().plusHours(hours));

        newCart.setItems(new ArrayList<>());
        cartCounterService.increment();
        return cartRepository.save(newCart);
    }

    private boolean isSameProduct(CartItem item, UUID prodId, UUID customId) {
        if (prodId != null && prodId.equals(item.getProductVariantId())) return true;
        if (customId != null && customId.equals(item.getCustomCakeId())) return true;
        return false;
    }

}
