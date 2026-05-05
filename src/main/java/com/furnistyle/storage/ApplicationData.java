package com.furnistyle.storage;

import com.furnistyle.model.furniture.Furniture;
import com.furnistyle.model.order.Order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ApplicationData implements Serializable {
    private final List<Furniture> furnitureList;
    private final List<Order> orders;

    public ApplicationData(List<Furniture> furnitureList, List<Order> orders) {
        this.furnitureList = new ArrayList<>(furnitureList);
        this.orders = new ArrayList<>(orders);
    }

    public List<Furniture> getFurnitureList() {
        return furnitureList;
    }

    public List<Order> getOrders() {
        return orders;
    }
}
