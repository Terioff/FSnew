package com.furnistyle.storage;

import com.furnistyle.model.furniture.Furniture;
import com.furnistyle.model.order.Order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public record ApplicationData(List<Furniture> furnitureList, List<Order> orders) implements Serializable {
    public ApplicationData(List<Furniture> furnitureList, List<Order> orders) {
        this.furnitureList = new ArrayList<>(furnitureList);
        this.orders = new ArrayList<>(orders);
    }
}
