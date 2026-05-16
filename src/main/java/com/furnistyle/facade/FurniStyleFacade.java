package com.furnistyle.facade;

import com.furnistyle.listener.DataChangeListener;
import com.furnistyle.model.furniture.Furniture;
import com.furnistyle.model.furniture.FurnitureCategory;
import com.furnistyle.model.furniture.FurnitureStatus;
import com.furnistyle.model.order.Order;
import com.furnistyle.model.order.OrderItem;
import com.furnistyle.service.FurnitureService;
import com.furnistyle.service.OrderService;
import com.furnistyle.storage.ApplicationData;
import com.furnistyle.storage.FileStorage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FurniStyleFacade {
    private final FurnitureService furnitureService;
    private final OrderService orderService;
    private final FileStorage fileStorage;
    private final List<DataChangeListener> listeners;

    public FurniStyleFacade() {
        this.furnitureService = new FurnitureService();
        this.orderService = new OrderService();
        this.fileStorage = new FileStorage();
        this.listeners = new ArrayList<>();
        addDemoData();
    }

    public void addListener(DataChangeListener listener) {
        listeners.add(listener);
    }

    public Furniture addFurniture(String name, FurnitureCategory category, double price, FurnitureStatus status) {
        return addFurniture(name, category, price, status, "");
    }

    public Furniture addFurniture(String name, FurnitureCategory category, double price, FurnitureStatus status, String description) {
        Furniture furniture = furnitureService.addFurniture(name, category, price, status, description);
        notifyListeners();
        return furniture;
    }

    public void updateFurniture(Furniture furniture, String name, FurnitureCategory category, double price, FurnitureStatus status) {
        updateFurniture(furniture, name, category, price, status, "");
    }

    public void updateFurniture(Furniture furniture, String name, FurnitureCategory category, double price, FurnitureStatus status, String description) {
        furnitureService.updateFurniture(furniture, name, category, price, status, description);
        notifyListeners();
    }

    public void archiveFurniture(Furniture furniture) {
        furnitureService.archiveFurniture(furniture);
        notifyListeners();
    }

    public Order createOrder(String clientName, String clientPhone, List<OrderItem> orderItems) {
        Order order = orderService.createOrder(clientName, clientPhone, orderItems);
        notifyListeners();
        return order;
    }

    public void updateOrder(Order order, String clientName, String clientPhone, List<OrderItem> orderItems) {
        orderService.updateOrder(order, clientName, clientPhone, orderItems);
        notifyListeners();
    }

    public void moveOrderToNextState(Order order) {
        orderService.moveOrderToNextState(order);
        notifyListeners();
    }

    public void rollbackOrderState(Order order) {
        orderService.rollbackOrderState(order);
        notifyListeners();
    }

    public void cancelOrder(Order order) {
        orderService.cancelOrder(order);
        notifyListeners();
    }

    public List<Furniture> getAllFurniture() {
        return furnitureService.getAllFurniture();
    }

    public List<Furniture> getOrderAvailableFurniture() {
        return furnitureService.getOrderAvailableFurniture();
    }

    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    public void saveToFile(File file) throws IOException {
        ApplicationData data = new ApplicationData(furnitureService.getAllFurniture(), orderService.getAllOrders());
        fileStorage.save(data, file);
    }

    public void loadFromFile(File file) throws IOException, ClassNotFoundException {
        ApplicationData data = fileStorage.load(file);
        furnitureService.replaceAll(data.furnitureList());
        orderService.replaceAll(data.orders());
        notifyListeners();
    }

    private void notifyListeners() {
        for (DataChangeListener listener : listeners) {
            listener.onDataChanged();
        }
    }

    private void addDemoData() {
        furnitureService.addFurniture("Диван Oslo", FurnitureCategory.SOFA, 55900, FurnitureStatus.AVAILABLE, "Тканевая обивка, раскладной механизм.");
        furnitureService.addFurniture("Шкаф Verona", FurnitureCategory.WARDROBE, 38900, FurnitureStatus.AVAILABLE, "Три секции, зеркальные двери.");
        furnitureService.addFurniture("Кухня Milano", FurnitureCategory.KITCHEN, 149000, FurnitureStatus.TO_ORDER, "Модульная кухня под индивидуальные размеры.");
        furnitureService.addFurniture("Стол Loft", FurnitureCategory.TABLE, 18900, FurnitureStatus.AVAILABLE, "Металлическое основание, деревянная столешница.");
        furnitureService.addFurniture("Зеркало Aura", FurnitureCategory.ACCESSORY, 7900, FurnitureStatus.AVAILABLE, "Настенное зеркало с подсветкой.");
    }
}
