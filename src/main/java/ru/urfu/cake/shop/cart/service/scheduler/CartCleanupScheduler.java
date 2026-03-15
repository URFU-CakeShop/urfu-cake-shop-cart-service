package ru.urfu.cake.shop.cart.service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.urfu.cake.shop.cart.service.repository.CartRepository;
import ru.urfu.cake.shop.cart.service.util.TimeUtil;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class CartCleanupScheduler {

    private final CartRepository cartRepository;

    // срабатывает каждый час
    @Scheduled(fixedRate = 3600000)
    public void cleanupExpiredCarts() {
        log.info("Запуск очистки просроченных корзин...");

        LocalDateTime now = TimeUtil.now();
        cartRepository.deleteExpiredCarts(now);

        log.info("Очистка завершена.");
    }
}
