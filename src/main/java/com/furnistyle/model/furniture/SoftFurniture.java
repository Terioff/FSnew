package com.furnistyle.model.furniture;

public class SoftFurniture extends Furniture {
    public SoftFurniture(String name, FurnitureCategory category, double price, FurnitureStatus status) {
        super(name, category, price, status);
    }

    @Override
    public String getTypeDescription() {
        return "Мягкая мебель";
    }
}
