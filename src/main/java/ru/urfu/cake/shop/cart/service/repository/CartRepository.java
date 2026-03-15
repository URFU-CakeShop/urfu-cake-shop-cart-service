package ru.urfu.cake.shop.cart.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.urfu.cake.shop.cart.service.entity.Carts;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Carts, UUID> {
    Optional<Carts> findByUserId(UUID userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Carts c WHERE c.expiresAt < :now")
    void deleteExpiredCarts(LocalDateTime now);
}
