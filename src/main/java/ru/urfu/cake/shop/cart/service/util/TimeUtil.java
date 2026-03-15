package ru.urfu.cake.shop.cart.service.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeUtil {
    private static final ZoneId ZONE = ZoneId.of("Europe/Moscow");

    public static LocalDateTime now() {
        return LocalDateTime.now(ZONE);
    }
}
