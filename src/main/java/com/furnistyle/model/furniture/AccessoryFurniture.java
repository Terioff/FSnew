package com.furnistyle.model.furniture;

public class AccessoryFurniture extends Furniture {
    public AccessoryFurniture(String name, FurnitureCategory category, double price, FurnitureStatus status) {
        super(name, category, price, status);
    }

    @Override
    public String getTypeDescription() {
        return "Аксессуар";
    }
}
