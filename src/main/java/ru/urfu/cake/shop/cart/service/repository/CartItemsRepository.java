package ru.urfu.cake.shop.cart.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.cake.shop.cart.service.entity.CartItem;

import java.util.UUID;

public interface CartItemsRepository extends JpaRepository<CartItem, UUID> {

}
