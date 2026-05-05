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
    private final List<Furniture> furnitureList;
    private OrderState state;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Order(Client client, List<Furniture> furnitureList) {
        this.id = UUID.randomUUID().toString();
        this.client = client;
        this.furnitureList = new ArrayList<>(furnitureList);
        this.state = new NewOrderState();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public List<Furniture> getFurnitureList() {
        return Collections.unmodifiableList(furnitureList);
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
        this.updatedAt = LocalDateTime.now();
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
        double totalPrice = 0;
        for (Furniture furniture : furnitureList) {
            totalPrice += furniture.getPrice();
        }
        return totalPrice;
    }

    public String getShortId() {
        return id.substring(0, 8);
    }

    public String getCreatedAtText() {
        return createdAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }
}
