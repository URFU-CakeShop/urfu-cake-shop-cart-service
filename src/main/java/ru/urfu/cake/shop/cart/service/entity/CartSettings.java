package ru.urfu.cake.shop.cart.service.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name= "cart_settings")
public class CartSettings {

    @Id
    private Integer id = 1;

    // Время жизни корзины в чесах (если к ней не кто не прикасался)
    @Column(name = "expiration_hours", nullable = false)
    private Integer expirationHours;

    // Максимальное количество предметов в корзине. По умолчанию стоит 50
    @Column(name = "max_items_count")
    private Integer maxItemsCount;

}
