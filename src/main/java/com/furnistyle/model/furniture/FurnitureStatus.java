package com.furnistyle.model.furniture;

public enum FurnitureStatus {
    AVAILABLE("В наличии"),
    TO_ORDER("Под заказ"),
    RESERVED("Зарезервирован"),
    SOLD("Продан"),
    ARCHIVED("В архиве");

    private final String title;

    FurnitureStatus(String title) {
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
