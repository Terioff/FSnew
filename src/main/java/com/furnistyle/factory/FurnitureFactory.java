package com.furnistyle.factory;

import com.furnistyle.model.furniture.AccessoryFurniture;
import com.furnistyle.model.furniture.CabinetFurniture;
import com.furnistyle.model.furniture.Furniture;
import com.furnistyle.model.furniture.FurnitureCategory;
import com.furnistyle.model.furniture.FurnitureStatus;
import com.furnistyle.model.furniture.SoftFurniture;

public class FurnitureFactory {
    public Furniture createFurniture(FurnitureCategory category, String name, double price, FurnitureStatus status) {
        if (category == FurnitureCategory.SOFA || category == FurnitureCategory.ARMCHAIR) {
            return new SoftFurniture(name, category, price, status);
        }

        if (category == FurnitureCategory.ACCESSORY) {
            return new AccessoryFurniture(name, category, price, status);
        }

        return new CabinetFurniture(name, category, price, status);
    }
}
