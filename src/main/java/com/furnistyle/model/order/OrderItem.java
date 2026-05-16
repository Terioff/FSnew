package com.furnistyle.model.order;

import com.furnistyle.model.furniture.Furniture;

import java.io.Serializable;

public record OrderItem(Furniture furniture, int quantity) implements Serializable {
    public OrderItem {
        if (furniture == null) {
            throw new IllegalArgumentException("Выберите мебель для позиции заказа.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Количество должно быть больше нуля.");
        }
    }

    public double getTotalPrice() {
        return furniture.getPrice() * quantity;
    }

    public String getDescription() {
        return quantity + " × " + furniture.getName();
    }
}
