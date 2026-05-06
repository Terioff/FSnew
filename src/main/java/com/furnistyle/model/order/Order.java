package com.furnistyle.model.order;

import com.furnistyle.model.client.Client;
import com.furnistyle.model.furniture.Furniture;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Order implements Serializable {
    private final String id;
    private final Client client;
    private final LocalDateTime createdAt;
    private List<Furniture> furnitureList;
    private List<OrderItem> orderItems;
    private List<OrderState> stateHistory;
    private OrderState state;
    private LocalDateTime updatedAt;

    public Order(Client client, List<Furniture> furnitureList) {
        this(client, furnitureList.stream()
                .map(furniture -> new OrderItem(furniture, 1))
                .collect(Collectors.toList()), 0);
    }

    private Order(Client client, List<OrderItem> orderItems, int ignored) {
        this.id = UUID.randomUUID().toString();
        this.client = client;
        this.orderItems = new ArrayList<>(orderItems);
        this.furnitureList = orderItems.stream()
                .map(OrderItem::getFurniture)
                .collect(Collectors.toCollection(ArrayList::new));
        this.stateHistory = new ArrayList<>();
        this.state = new NewOrderState();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = createdAt;
    }

    public static Order withItems(Client client, List<OrderItem> orderItems) {
        return new Order(client, orderItems, 0);
    }

    public String getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public List<Furniture> getFurnitureList() {
        ensureOrderItems();
        return Collections.unmodifiableList(furnitureList);
    }

    public List<OrderItem> getOrderItems() {
        ensureOrderItems();
        return Collections.unmodifiableList(orderItems);
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        ensureStateHistory();
        stateHistory.add(this.state);
        this.state = state;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean canRollbackState() {
        ensureStateHistory();
        return !stateHistory.isEmpty();
    }

    public void rollbackState() {
        ensureStateHistory();
        if (stateHistory.isEmpty()) {
            throw new IllegalStateException("У заказа нет предыдущего статуса для отката.");
        }
        state = stateHistory.remove(stateHistory.size() - 1);
        updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void moveToNextState() {
        state.next(this);
    }

    public void cancel() {
        state.cancel(this);
    }

    public boolean isFinished() {
        return state.isFinished();
    }

    public double getTotalPrice() {
        ensureOrderItems();
        double totalPrice = 0;
        for (OrderItem item : orderItems) {
            totalPrice += item.getTotalPrice();
        }
        return totalPrice;
    }

    public int getTotalQuantity() {
        ensureOrderItems();
        int totalQuantity = 0;
        for (OrderItem item : orderItems) {
            totalQuantity += item.getQuantity();
        }
        return totalQuantity;
    }

    public String getItemsText() {
        ensureOrderItems();
        return orderItems.stream()
                .map(OrderItem::getDescription)
                .collect(Collectors.joining("; "));
    }

    public String getShortId() {
        return id.substring(0, 8);
    }

    public String getCreatedAtText() {
        return createdAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    private void ensureOrderItems() {
        if (orderItems == null) {
            orderItems = new ArrayList<>();
            if (furnitureList != null) {
                for (Furniture furniture : furnitureList) {
                    orderItems.add(new OrderItem(furniture, 1));
                }
            }
        }
        if (furnitureList == null) {
            furnitureList = orderItems.stream()
                    .map(OrderItem::getFurniture)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
    }

    private void ensureStateHistory() {
        if (stateHistory == null) {
            stateHistory = new ArrayList<>();
        }
    }
}
