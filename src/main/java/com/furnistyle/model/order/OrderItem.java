package com.furnistyle.model.order;

import com.furnistyle.model.furniture.Furniture;

import java.io.Serializable;

public record OrderItem(Furniture furniture, int quantity, double priceAtOrder) implements Serializable {
    public OrderItem {
        if (furniture == null) {
            throw new IllegalArgumentException("Выберите мебель для позиции заказа.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Количество должно быть больше нуля.");
        }
    }

    public OrderItem(Furniture furniture, int quantity) {
        this(furniture, quantity, furniture.getPrice());
    }

    public double getTotalPrice() {
        return priceAtOrder * quantity;
    }

    public String getDescription() {
        return quantity + " × " + furniture.getName() + " (" + priceAtOrder + " руб.)";
    }

    public int getQuantity() {
        return quantity;
    }

    public Furniture getFurniture() {
        return furniture;
    }


}
