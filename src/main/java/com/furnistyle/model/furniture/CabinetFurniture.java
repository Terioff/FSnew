package com.furnistyle.model.furniture;

public class CabinetFurniture extends Furniture {
    public CabinetFurniture(String name, FurnitureCategory category, double price, FurnitureStatus status) {
        super(name, category, price, status);
    }

    public CabinetFurniture(String name, FurnitureCategory category, double price, FurnitureStatus status, String description) {
        super(name, category, price, status, description);
    }

    @Override
    public String getTypeDescription() {
        return "Корпусная мебель";
    }
}
