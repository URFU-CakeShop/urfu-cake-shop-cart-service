package ru.urfu.cake.shop.cart.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.cake.shop.cart.service.dto.AddCartItemDto;
import ru.urfu.cake.shop.cart.service.entity.CartItem;
import ru.urfu.cake.shop.cart.service.entity.Carts;
import ru.urfu.cake.shop.cart.service.exception.CartNotFoundException;
import ru.urfu.cake.shop.cart.service.exception.ItemNotFoundException;
import ru.urfu.cake.shop.cart.service.exception.NotYourCartException;
import ru.urfu.cake.shop.cart.service.model.CartItemModel;
import ru.urfu.cake.shop.cart.service.model.CartsModel;
import ru.urfu.cake.shop.cart.service.repository.CartItemsRepository;
import ru.urfu.cake.shop.cart.service.repository.CartRepository;
import ru.urfu.cake.shop.cart.service.util.TimeUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartsServiceImpl implements CartsService {

    private final CartRepository cartRepository;
    private final CartItemsRepository cartItemsRepository;

    @Override
    public Carts getCartByUserId(UUID userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(userId));
    }

    @Override
    @Transactional
    public Carts addItemToCart(AddCartItemDto dto) {
        Carts cart = getCartByUserId(dto.getUserId());

        // ПРОДЛЕВАЕМ СРОК ЖИЗНИ: каждый раз + 24 часа от текущего момента
        cart.setExpiresAt(TimeUtil.now().plusDays(1));

        // Ищем, есть ли уже такой товар
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> isSameProduct(item, dto.getProductVariantId(), dto.getCustomCakeId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + dto.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProductVariantId(dto.getProductVariantId());
            newItem.setCustomCakeId(dto.getCustomCakeId());
            newItem.setQuantity(dto.getQuantity());
            newItem.setPrice(BigDecimal.valueOf(1000)); // Временно хардкодим цену пока не готов сервис каталога ;)

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
    }

    // --- Вспомогательные методы ---

    private Carts createNewCart(UUID userId) {
        Carts newCart = new Carts();
        newCart.setUserId(userId);
        newCart.setStatus("ACTIVE");
        newCart.setCreatedAt(TimeUtil.now());


        newCart.setExpiresAt(TimeUtil.now().plusDays(1)); // Допустим, корзина живет 24 часа

        newCart.setItems(new ArrayList<>());
        return cartRepository.save(newCart);
    }

    private boolean isSameProduct(CartItem item, UUID prodId, UUID customId) {
        if (prodId != null && prodId.equals(item.getProductVariantId())) return true;
        if (customId != null && customId.equals(item.getCustomCakeId())) return true;
        return false;
    }

    // --- МАППИНГ ENTITY -> MODEL ---

    public CartsModel toModel(Carts cart) {
        BigDecimal total = cart.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartsModel.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .status(cart.getStatus())
                .expiresAt(cart.getExpiresAt())
                .createdAt(cart.getCreatedAt())
                .totalAmount(total)
                .items(cart.getItems().stream().map(this::toItemModel).toList())
                .build();
    }

    private CartItemModel toItemModel(CartItem item) {
        return CartItemModel.builder()
                .id(item.getId())
                .cartId(item.getCart().getId())
                .productVariantId(item.getProductVariantId())
                .customCakeId(item.getCustomCakeId())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build();
    }
}
