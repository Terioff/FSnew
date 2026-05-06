package com.furnistyle.service;

import com.furnistyle.model.client.Client;
import com.furnistyle.model.furniture.Furniture;
import com.furnistyle.model.order.Order;
import com.furnistyle.model.order.OrderItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderService implements Serializable {
    private final List<Order> orders = new ArrayList<>();

    public Order createOrder(String clientName, String clientPhone, List<OrderItem> orderItems) {
        if (clientName == null || clientName.isBlank()) {
            throw new IllegalArgumentException("Введите имя клиента.");
        }

        if (clientPhone == null || clientPhone.isBlank()) {
            throw new IllegalArgumentException("Введите телефон клиента.");
        }

        if (!isValidInternationalPhone(clientPhone)) {
            throw new IllegalArgumentException("Введите телефон в международном формате: плюс и от 4 до 15 цифр.");
        }

        if (orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException("Выберите хотя бы одну позицию мебели.");
        }

        for (OrderItem item : orderItems) {
            Furniture furniture = item.getFurniture();
            if (!furniture.canBeOrdered()) {
                throw new IllegalArgumentException("Товар недоступен для заказа: " + furniture.getName());
            }
        }

        Client client = new Client(clientName, clientPhone);
        Order order = Order.withItems(client, orderItems);
        orders.add(order);

        return order;
    }

    public void moveOrderToNextState(Order order) {
        order.moveToNextState();
    }

    public void rollbackOrderState(Order order) {
        order.rollbackState();
    }

    public void cancelOrder(Order order) {
        order.cancel();
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

    private boolean isValidInternationalPhone(String phone) {
        return phone.matches("\\+\\d{4,15}");
    }
}
