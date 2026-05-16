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
        this(client, getItemsFromFurniture(furnitureList), 0);
    }

    private Order(Client client, List<OrderItem> orderItems, int ignored) {
        this.id = UUID.randomUUID().toString();
        this.client = client;
        this.orderItems = new ArrayList<>(orderItems);
        this.furnitureList = getFurnitureList(orderItems);
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
        checkOrderItems();
        return Collections.unmodifiableList(furnitureList);
    }

    public List<OrderItem> getOrderItems() {
        checkOrderItems();
        return Collections.unmodifiableList(orderItems);
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        checkStateList();
        stateHistory.add(this.state);
        this.state = state;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean canRollbackState() {
        checkStateList();
        return !stateHistory.isEmpty();
    }

    public void rollbackState() {
        checkStateList();
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

    public void updateDetails(String clientName, String clientPhone, List<OrderItem> orderItems) {
        if (clientName == null || clientName.isBlank()) {
            throw new IllegalArgumentException("Введите имя клиента.");
        }
        if (clientPhone == null || clientPhone.isBlank()) {
            throw new IllegalArgumentException("Введите телефон клиента.");
        }
        if (orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException("Выберите хотя бы одну позицию мебели.");
        }
        client.setFullName(clientName);
        client.setPhone(clientPhone);
        this.orderItems = new ArrayList<>(orderItems);
        this.furnitureList = getFurnitureList(orderItems);
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isFinished() {
        return state.isFinished();
    }

    public double getTotalPrice() {
        checkOrderItems();
        double totalPrice = 0;
        for (OrderItem item : orderItems) {
            totalPrice += item.getTotalPrice();
        }
        return totalPrice;
    }

    public int getTotalQuantity() {
        checkOrderItems();
        int totalQuantity = 0;
        for (OrderItem item : orderItems) {
            totalQuantity += item.getQuantity();
        }
        return totalQuantity;
    }

    public String getItemsText() {
        checkOrderItems();
        StringBuilder text = new StringBuilder();
        for (OrderItem item : orderItems) {
            if (text.length() > 0) {
                text.append("; ");
            }
            text.append(item.getDescription());
        }
        return text.toString();
    }

    public String getShortId() {
        return id.substring(0, 8);
    }

    public String getCreatedAtText() {
        return createdAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }


    private static List<OrderItem> getItemsFromFurniture(List<Furniture> furnitureList) {
        List<OrderItem> items = new ArrayList<>();
        for (Furniture furniture : furnitureList) {
            items.add(new OrderItem(furniture, 1));
        }
        return items;
    }

    private void checkOrderItems() {
        if (orderItems == null) {
            orderItems = new ArrayList<>();
            if (furnitureList != null) {
                for (Furniture furniture : furnitureList) {
                    orderItems.add(new OrderItem(furniture, 1));
                }
            }
        }
        if (furnitureList == null) {
            furnitureList = getFurnitureList(orderItems);
        }
    }


    private static List<Furniture> getFurnitureList(List<OrderItem> orderItems) {
        List<Furniture> result = new ArrayList<>();
        for (OrderItem item : orderItems) {
            result.add(item.getFurniture());
        }
        return result;
    }

    private void checkStateList() {
        if (stateHistory == null) {
            stateHistory = new ArrayList<>();
        }
    }
}
