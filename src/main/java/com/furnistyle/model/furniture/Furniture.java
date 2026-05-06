package com.furnistyle.model.furniture;

import java.io.Serializable;
import java.util.UUID;

public abstract class Furniture implements Serializable {
    private final String id;
    private String name;
    private FurnitureCategory category;
    private double price;
    private FurnitureStatus status;
    private String description;

    public Furniture(String name, FurnitureCategory category, double price, FurnitureStatus status) {
        this(name, category, price, status, "");
    }

    public Furniture(String name, FurnitureCategory category, double price, FurnitureStatus status, String description) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.category = category;
        this.price = price;
        this.status = status;
        this.description = description == null ? "" : description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FurnitureCategory getCategory() {
        return category;
    }

    public void setCategory(FurnitureCategory category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public FurnitureStatus getStatus() {
        return status;
    }

    public void setStatus(FurnitureStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description;
    }

    public boolean canBeOrdered() {
        return status == FurnitureStatus.AVAILABLE || status == FurnitureStatus.TO_ORDER;
    }

    public abstract String getTypeDescription();

    @Override
    public String toString() {
        return name + " (" + category + ", " + status + ")";
    }
}
