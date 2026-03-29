package ru.urfu.cake.shop.cart.service.service.metrics;


import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.urfu.cake.shop.cart.service.repository.CartRepository;

import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class CartCounterService {

    private final AtomicLong currentCartCount = new AtomicLong(0);
    private final CartRepository cartRepository;
    private final MeterRegistry meterRegistry;

    @PostConstruct
    public void init(){
        currentCartCount.set(cartRepository.count());

        Gauge.builder("cart_active_count", currentCartCount, AtomicLong::get)
                .description("Количество активных корзин")
                .register(meterRegistry);
    }

    public void increment(){
        currentCartCount.incrementAndGet();
    }

    public void decrement() {
        currentCartCount.decrementAndGet();
    }

    public void subtract(int count) {
        currentCartCount.addAndGet(-count);
    }

    public void set(long value) {
        currentCartCount.set(value);
    }

}
