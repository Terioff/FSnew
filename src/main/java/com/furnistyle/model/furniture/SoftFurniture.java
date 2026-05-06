package com.furnistyle.model.furniture;

public class SoftFurniture extends Furniture {
    public SoftFurniture(String name, FurnitureCategory category, double price, FurnitureStatus status) {
        super(name, category, price, status);
    }

    public SoftFurniture(String name, FurnitureCategory category, double price, FurnitureStatus status, String description) {
        super(name, category, price, status, description);
    }

    @Override
    public String getTypeDescription() {
        return "Мягкая мебель";
    }
}
