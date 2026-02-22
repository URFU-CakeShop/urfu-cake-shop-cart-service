package ru.urfu.cake.shop.cart.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CakeShopCartServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CakeShopCartServiceApplication.class, args);
    }

}
