package com.furnistyle.service;

import com.furnistyle.factory.FurnitureFactory;
import com.furnistyle.model.furniture.Furniture;
import com.furnistyle.model.furniture.FurnitureCategory;
import com.furnistyle.model.furniture.FurnitureStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FurnitureService implements Serializable {
    private final List<Furniture> furnitureList = new ArrayList<>();
    private final FurnitureFactory furnitureFactory = new FurnitureFactory();

    public Furniture addFurniture(String name, FurnitureCategory category, double price, FurnitureStatus status) {
        return addFurniture(name, category, price, status, "");
    }

    public Furniture addFurniture(String name, FurnitureCategory category, double price, FurnitureStatus status, String description) {
        validateFurnitureData(name, price);
        Furniture furniture = furnitureFactory.createFurniture(category, name, price, status, description);
        furnitureList.add(furniture);
        return furniture;
    }

    public void updateFurniture(Furniture furniture, String name, FurnitureCategory category, double price, FurnitureStatus status) {
        updateFurniture(furniture, name, category, price, status, "");
    }

    public void updateFurniture(Furniture furniture, String name, FurnitureCategory category, double price, FurnitureStatus status, String description) {
        validateFurnitureData(name, price);
        furniture.setName(name);
        furniture.setCategory(category);
        furniture.setPrice(price);
        furniture.setStatus(status);
        furniture.setDescription(description);
    }

    public void archiveFurniture(Furniture furniture) {
        furniture.setStatus(FurnitureStatus.ARCHIVED);
    }

    public List<Furniture> getAllFurniture() {
        return Collections.unmodifiableList(furnitureList);
    }

    public List<Furniture> getOrderAvailableFurniture() {
        List<Furniture> result = new ArrayList<>();
        for (Furniture furniture : furnitureList) {
            if (furniture.canBeOrdered()) {
                result.add(furniture);
            }
        }
        return result;
    }

    public void replaceAll(List<Furniture> furniture) {
        furnitureList.clear();
        furnitureList.addAll(furniture);
    }

    private void validateFurnitureData(String name, double price) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Введите название мебели.");
        }

        if (price <= 0) {
            throw new IllegalArgumentException("Стоимость должна быть больше нуля.");
        }
    }
}
