package com.furnistyle.facade;

import com.furnistyle.listener.DataChangeListener;
import com.furnistyle.model.furniture.Furniture;
import com.furnistyle.model.furniture.FurnitureCategory;
import com.furnistyle.model.furniture.FurnitureStatus;
import com.furnistyle.model.order.Order;
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
        Furniture furniture = furnitureService.addFurniture(name, category, price, status);
        notifyListeners();
        return furniture;
    }

    public void updateFurniture(Furniture furniture, String name, FurnitureCategory category, double price, FurnitureStatus status) {
        furnitureService.updateFurniture(furniture, name, category, price, status);
        notifyListeners();
    }

    public void archiveFurniture(Furniture furniture) {
        furnitureService.archiveFurniture(furniture);
        notifyListeners();
    }

    public Order createOrder(String clientName, String clientPhone, List<Furniture> selectedFurniture) {
        Order order = orderService.createOrder(clientName, clientPhone, selectedFurniture);
        notifyListeners();
        return order;
    }

    public void moveOrderToNextState(Order order) {
        orderService.moveOrderToNextState(order);
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
        furnitureService.replaceAll(data.getFurnitureList());
        orderService.replaceAll(data.getOrders());
        notifyListeners();
    }

    private void notifyListeners() {
        for (DataChangeListener listener : listeners) {
            listener.onDataChanged();
        }
    }

    private void addDemoData() {
        furnitureService.addFurniture("Диван Oslo", FurnitureCategory.SOFA, 55900, FurnitureStatus.AVAILABLE);
        furnitureService.addFurniture("Шкаф Verona", FurnitureCategory.WARDROBE, 38900, FurnitureStatus.AVAILABLE);
        furnitureService.addFurniture("Кухня Milano", FurnitureCategory.KITCHEN, 149000, FurnitureStatus.TO_ORDER);
        furnitureService.addFurniture("Стол Loft", FurnitureCategory.TABLE, 18900, FurnitureStatus.AVAILABLE);
        furnitureService.addFurniture("Зеркало Aura", FurnitureCategory.ACCESSORY, 7900, FurnitureStatus.AVAILABLE);
    }
}
