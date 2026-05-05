package com.furnistyle.service;

import com.furnistyle.model.client.Client;
import com.furnistyle.model.furniture.Furniture;
import com.furnistyle.model.furniture.FurnitureStatus;
import com.furnistyle.model.order.Order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderService implements Serializable {
    private final List<Order> orders = new ArrayList<>();

    public Order createOrder(String clientName, String clientPhone, List<Furniture> selectedFurniture) {
        if (clientName == null || clientName.isBlank()) {
            throw new IllegalArgumentException("Введите имя клиента.");
        }

        if (clientPhone == null || clientPhone.isBlank()) {
            throw new IllegalArgumentException("Введите телефон клиента.");
        }

        if (selectedFurniture == null || selectedFurniture.isEmpty()) {
            throw new IllegalArgumentException("Выберите хотя бы одну позицию мебели.");
        }

        for (Furniture furniture : selectedFurniture) {
            if (!furniture.canBeOrdered()) {
                throw new IllegalArgumentException("Товар недоступен для заказа: " + furniture.getName());
            }
        }

        Client client = new Client(clientName, clientPhone);
        Order order = new Order(client, selectedFurniture);
        orders.add(order);

        for (Furniture furniture : selectedFurniture) {
            if (furniture.getStatus() == FurnitureStatus.AVAILABLE) {
                furniture.setStatus(FurnitureStatus.RESERVED);
            }
        }

        return order;
    }

    public void moveOrderToNextState(Order order) {
        order.moveToNextState();
        if (order.isFinished()) {
            markOrderFurnitureAsSold(order);
        }
    }

    public void cancelOrder(Order order) {
        order.cancel();
        for (Furniture furniture : order.getFurnitureList()) {
            if (furniture.getStatus() == FurnitureStatus.RESERVED) {
                furniture.setStatus(FurnitureStatus.AVAILABLE);
            }
        }
    }

    public List<Order> getAllOrders() {
        return Collections.unmodifiableList(orders);
    }

    public List<Order> getActiveOrders() {
        List<Order> result = new ArrayList<>();
        for (Order order : orders) {
            if (!order.isFinished()) {
                result.add(order);
            }
        }
        return result;
    }

    public List<Order> getFinishedOrders() {
        List<Order> result = new ArrayList<>();
        for (Order order : orders) {
            if (order.isFinished()) {
                result.add(order);
            }
        }
        return result;
    }

    public void replaceAll(List<Order> loadedOrders) {
        orders.clear();
        orders.addAll(loadedOrders);
    }

    private void markOrderFurnitureAsSold(Order order) {
        for (Furniture furniture : order.getFurnitureList()) {
            furniture.setStatus(FurnitureStatus.SOLD);
        }
    }
}
