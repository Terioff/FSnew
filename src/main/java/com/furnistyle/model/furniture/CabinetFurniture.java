package com.furnistyle.model.furniture;

public class CabinetFurniture extends Furniture {
    public CabinetFurniture(String name, FurnitureCategory category, double price, FurnitureStatus status) {
        super(name, category, price, status);
    }

    @Override
    public String getTypeDescription() {
        return "Корпусная мебель";
    }
}
