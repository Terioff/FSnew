package com.furnistyle.model.furniture;

public enum FurnitureCategory {
    SOFA("Диван"),
    ARMCHAIR("Кресло"),
    TABLE("Стол"),
    WARDROBE("Шкаф"),
    KITCHEN("Кухня"),
    ACCESSORY("Аксессуар");

    private final String title;

    FurnitureCategory(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return title;
    }
}
