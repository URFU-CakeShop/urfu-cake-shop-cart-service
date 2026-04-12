package ru.urfu.cake.shop.cart.service.client.Response;


import lombok.Data;

@Data
public class BaseProductResponse<T> {
    private boolean success;
    private T data;
    private String message;
}
