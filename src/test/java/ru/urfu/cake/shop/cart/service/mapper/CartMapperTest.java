package ru.urfu.cake.shop.cart.service.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.urfu.cake.shop.cart.service.client.ProductClient;
import ru.urfu.cake.shop.cart.service.entity.CartItem;
import ru.urfu.cake.shop.cart.service.entity.Carts;
import ru.urfu.cake.shop.cart.service.model.CartItemModel;
import ru.urfu.cake.shop.cart.service.model.CartsModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartMapperTest {

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private CartMapper cartMapper;

    @Test
    void toItemModel_shouldFetchPriceFromProductClient() {
        UUID variantId = UUID.randomUUID();
        when(productClient.getPrice(variantId)).thenReturn(BigDecimal.valueOf(799.50));

        CartItem item = new CartItem();
        item.setId(UUID.randomUUID());
        item.setProductVariantId(variantId);
        item.setQuantity(3);

        Carts cart = new Carts();
        cart.setId(UUID.randomUUID());
        item.setCart(cart);

        CartItemModel model = cartMapper.toItemModel(item);

        assertThat(model.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(799.50));
        assertThat(model.getQuantity()).isEqualTo(3);
        assertThat(model.getProductVariantId()).isEqualTo(variantId);
    }

    @Test
    void toItemModel_shouldReturnZero_whenProductVariantIsNull() {
        CartItem item = new CartItem();
        item.setId(UUID.randomUUID());
        item.setProductVariantId(null);
        item.setCustomCakeId(UUID.randomUUID());
        item.setQuantity(1);

        Carts cart = new Carts();
        cart.setId(UUID.randomUUID());
        item.setCart(cart);

        CartItemModel model = cartMapper.toItemModel(item);

        assertThat(model.getPrice()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(model.getCustomCakeId()).isNotNull();
        verifyNoInteractions(productClient);
    }

    @Test
    void toModel_shouldAggregateTotalFromItems() {
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        when(productClient.getPrice(a)).thenReturn(BigDecimal.valueOf(1000));
        when(productClient.getPrice(b)).thenReturn(BigDecimal.valueOf(250));

        Carts cart = new Carts();
        cart.setId(UUID.randomUUID());

        CartItem first = new CartItem();
        first.setId(UUID.randomUUID());
        first.setProductVariantId(a);
        first.setQuantity(2);
        first.setCart(cart);

        CartItem second = new CartItem();
        second.setId(UUID.randomUUID());
        second.setProductVariantId(b);
        second.setQuantity(4);
        second.setCart(cart);

        cart.setItems(List.of(first, second));

        CartsModel result = cartMapper.toModel(cart);

        assertThat(result.getTotalAmount())
                .as("2 * 1000 + 4 * 250 = 3000")
                .isEqualByComparingTo(BigDecimal.valueOf(3000));
        assertThat(result.getItems()).hasSize(2);
        assertThat(result.getItems().get(0).getPrice()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        assertThat(result.getItems().get(1).getPrice()).isEqualByComparingTo(BigDecimal.valueOf(250));
    }

    @Test
    void toModel_shouldReturnZeroTotal_forEmptyCart() {
        Carts cart = new Carts();
        cart.setId(UUID.randomUUID());
        cart.setItems(List.of());

        CartsModel result = cartMapper.toModel(cart);

        assertThat(result.getTotalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getItems()).isEmpty();
        verifyNoInteractions(productClient);
    }

    @Test
    void toModel_shouldHandleMixedNullAndNonNullProductVariants() {
        UUID variant = UUID.randomUUID();
        when(productClient.getPrice(variant)).thenReturn(BigDecimal.valueOf(500));

        Carts cart = new Carts();
        cart.setId(UUID.randomUUID());

        CartItem withProduct = new CartItem();
        withProduct.setId(UUID.randomUUID());
        withProduct.setProductVariantId(variant);
        withProduct.setQuantity(1);
        withProduct.setCart(cart);

        CartItem customOnly = new CartItem();
        customOnly.setId(UUID.randomUUID());
        customOnly.setProductVariantId(null);
        customOnly.setCustomCakeId(UUID.randomUUID());
        customOnly.setQuantity(2);
        customOnly.setCart(cart);

        cart.setItems(List.of(withProduct, customOnly));

        CartsModel result = cartMapper.toModel(cart);

        assertThat(result.getTotalAmount())
                .as("1 * 500 + 2 * 0 = 500")
                .isEqualByComparingTo(BigDecimal.valueOf(500));
        assertThat(result.getItems().get(0).getPrice()).isEqualByComparingTo(BigDecimal.valueOf(500));
        assertThat(result.getItems().get(1).getPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
