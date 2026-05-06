package com.furnistyle.factory;

import com.furnistyle.model.furniture.*;

public class FurnitureFactory {
    public Furniture createFurniture(FurnitureCategory category, String name, double price, FurnitureStatus status) {
        return createFurniture(category, name, price, status, "");
    }

    public Furniture createFurniture(FurnitureCategory category, String name, double price, FurnitureStatus status, String description) {
        if (category == FurnitureCategory.SOFA || category == FurnitureCategory.ARMCHAIR) {
            return new SoftFurniture(name, category, price, status, description);
        }

        if (category == FurnitureCategory.ACCESSORY) {
            return new AccessoryFurniture(name, category, price, status, description);
        }

        return new CabinetFurniture(name, category, price, status, description);
    }
}
