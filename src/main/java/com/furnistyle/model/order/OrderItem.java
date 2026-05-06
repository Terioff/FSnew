package com.furnistyle.model.order;

import com.furnistyle.model.furniture.Furniture;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private final Furniture furniture;
    private final int quantity;

    public OrderItem(Furniture furniture, int quantity) {
        if (furniture == null) {
            throw new IllegalArgumentException("Выберите мебель для позиции заказа.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Количество должно быть больше нуля.");
        }
        this.furniture = furniture;
        this.quantity = quantity;
    }

    public Furniture getFurniture() {
        return furniture;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return furniture.getPrice() * quantity;
    }

    public String getDescription() {
        return quantity + " × " + furniture.getName();
    }
}
