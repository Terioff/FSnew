package com.furnistyle.model.furniture;

public class AccessoryFurniture extends Furniture {
    public AccessoryFurniture(String name, FurnitureCategory category, double price, FurnitureStatus status) {
        super(name, category, price, status);
    }

    public AccessoryFurniture(String name, FurnitureCategory category, double price, FurnitureStatus status, String description) {
        super(name, category, price, status, description);
    }

    @Override
    public String getTypeDescription() {
        return "Аксессуар";
    }
}
