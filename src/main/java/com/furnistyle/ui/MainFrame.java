package com.furnistyle.ui;

import com.furnistyle.facade.FurniStyleFacade;
import com.furnistyle.listener.DataChangeListener;
import com.furnistyle.model.furniture.Furniture;
import com.furnistyle.model.furniture.FurnitureCategory;
import com.furnistyle.model.furniture.FurnitureStatus;
import com.furnistyle.model.order.Order;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame implements DataChangeListener {
    private final FurniStyleFacade facade;
    private final JTable furnitureTable;
    private final JTable orderTable;
    private final JLabel summaryLabel;

    public MainFrame(FurniStyleFacade facade) {
        super("FurniStyle - мебельный салон");
        this.facade = facade;
        this.furnitureTable = new JTable();
        this.orderTable = new JTable();
        this.summaryLabel = new JLabel();

        facade.addListener(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(createTopPanel(), BorderLayout.NORTH);
        add(createTabs(), BorderLayout.CENTER);
        add(summaryLabel, BorderLayout.SOUTH);
        refreshTables();
    }

    @Override
    public void onDataChanged() {
        refreshTables();
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel();
        JButton saveButton = new JButton("Сохранить");
        JButton loadButton = new JButton("Загрузить");

        saveButton.addActionListener(event -> saveData());
        loadButton.addActionListener(event -> loadData());

        panel.add(new JLabel("Информационная система FurniStyle"));
        panel.add(saveButton);
        panel.add(loadButton);
        return panel;
    }

    private JTabbedPane createTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Каталог мебели", createFurniturePanel());
        tabs.addTab("Заказы", createOrderPanel());
        return tabs;
    }

    private JPanel createFurniturePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel buttons = new JPanel();
        JButton addButton = new JButton("Добавить мебель");
        JButton editButton = new JButton("Редактировать");
        JButton archiveButton = new JButton("В архив");

        furnitureTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        addButton.addActionListener(event -> showFurnitureDialog(null));
        editButton.addActionListener(event -> editSelectedFurniture());
        archiveButton.addActionListener(event -> archiveSelectedFurniture());

        buttons.add(addButton);
        buttons.add(editButton);
        buttons.add(archiveButton);
        panel.add(new JScrollPane(furnitureTable), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel buttons = new JPanel();
        JButton addButton = new JButton("Создать заказ");
        JButton nextButton = new JButton("Следующий статус");
        JButton cancelButton = new JButton("Отменить заказ");

        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        addButton.addActionListener(event -> showOrderDialog());
        nextButton.addActionListener(event -> moveSelectedOrder());
        cancelButton.addActionListener(event -> cancelSelectedOrder());

        buttons.add(addButton);
        buttons.add(nextButton);
        buttons.add(cancelButton);
        panel.add(new JScrollPane(orderTable), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private void showFurnitureDialog(Furniture furniture) {
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JComboBox<FurnitureCategory> categoryBox = new JComboBox<>(FurnitureCategory.values());
        JComboBox<FurnitureStatus> statusBox = new JComboBox<>(FurnitureStatus.values());

        if (furniture != null) {
            nameField.setText(furniture.getName());
            priceField.setText(String.valueOf(furniture.getPrice()));
            categoryBox.setSelectedItem(furniture.getCategory());
            statusBox.setSelectedItem(furniture.getStatus());
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(new JLabel("Название:"));
        panel.add(nameField);
        panel.add(new JLabel("Категория:"));
        panel.add(categoryBox);
        panel.add(new JLabel("Стоимость:"));
        panel.add(priceField);
        panel.add(new JLabel("Статус:"));
        panel.add(statusBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Мебель", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            double price = Double.parseDouble(priceField.getText().trim());
            FurnitureCategory category = (FurnitureCategory) categoryBox.getSelectedItem();
            FurnitureStatus status = (FurnitureStatus) statusBox.getSelectedItem();

            if (furniture == null) {
                facade.addFurniture(nameField.getText(), category, price, status);
            } else {
                facade.updateFurniture(furniture, nameField.getText(), category, price, status);
            }
        } catch (NumberFormatException exception) {
            showError("Стоимость должна быть числом.");
        } catch (RuntimeException exception) {
            showError(exception.getMessage());
        }
    }

    private void showOrderDialog() {
        List<Furniture> availableFurniture = facade.getOrderAvailableFurniture();
        if (availableFurniture.isEmpty()) {
            showError("Нет доступной мебели для заказа.");
            return;
        }

        JTextField clientNameField = new JTextField();
        JTextField phoneField = new JTextField();
        JPanel furniturePanel = new JPanel(new GridLayout(0, 1));
        List<JCheckBox> checkBoxes = new ArrayList<>();

        for (Furniture furniture : availableFurniture) {
            JCheckBox checkBox = new JCheckBox(furniture.getName() + " - " + furniture.getPrice() + " руб. (" + furniture.getStatus() + ")");
            checkBoxes.add(checkBox);
            furniturePanel.add(checkBox);
        }

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel clientPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        clientPanel.add(new JLabel("Клиент:"));
        clientPanel.add(clientNameField);
        clientPanel.add(new JLabel("Телефон:"));
        clientPanel.add(phoneField);
        panel.add(clientPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(furniturePanel), BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        int result = JOptionPane.showConfirmDialog(this, panel, "Новый заказ", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        List<Furniture> selectedFurniture = new ArrayList<>();
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                selectedFurniture.add(availableFurniture.get(i));
            }
        }

        try {
            facade.createOrder(clientNameField.getText(), phoneField.getText(), selectedFurniture);
        } catch (RuntimeException exception) {
            showError(exception.getMessage());
        }
    }

    private void editSelectedFurniture() {
        Furniture furniture = getSelectedFurniture();
        if (furniture == null) {
            showError("Выберите мебель в таблице.");
            return;
        }
        showFurnitureDialog(furniture);
    }

    private void archiveSelectedFurniture() {
        Furniture furniture = getSelectedFurniture();
        if (furniture == null) {
            showError("Выберите мебель в таблице.");
            return;
        }
        facade.archiveFurniture(furniture);
    }

    private void moveSelectedOrder() {
        Order order = getSelectedOrder();
        if (order == null) {
            showError("Выберите заказ в таблице.");
            return;
        }

        try {
            facade.moveOrderToNextState(order);
        } catch (RuntimeException exception) {
            showError(exception.getMessage());
        }
    }

    private void cancelSelectedOrder() {
        Order order = getSelectedOrder();
        if (order == null) {
            showError("Выберите заказ в таблице.");
            return;
        }

        try {
            facade.cancelOrder(order);
        } catch (RuntimeException exception) {
            showError(exception.getMessage());
        }
    }

    private Furniture getSelectedFurniture() {
        int row = furnitureTable.getSelectedRow();
        if (row < 0) {
            return null;
        }
        int modelRow = furnitureTable.convertRowIndexToModel(row);
        return facade.getAllFurniture().get(modelRow);
    }

    private Order getSelectedOrder() {
        int row = orderTable.getSelectedRow();
        if (row < 0) {
            return null;
        }
        int modelRow = orderTable.convertRowIndexToModel(row);
        return facade.getAllOrders().get(modelRow);
    }

    private void refreshTables() {
        refreshFurnitureTable();
        refreshOrderTable();
        summaryLabel.setText("  Мебели в каталоге: " + facade.getAllFurniture().size() + " | Заказов: " + facade.getAllOrders().size());
    }

    private void refreshFurnitureTable() {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Название", "Тип", "Категория", "Стоимость", "Статус"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Furniture furniture : facade.getAllFurniture()) {
            model.addRow(new Object[]{
                    furniture.getName(),
                    furniture.getTypeDescription(),
                    furniture.getCategory(),
                    furniture.getPrice(),
                    furniture.getStatus()
            });
        }
        furnitureTable.setModel(model);
    }

    private void refreshOrderTable() {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"№", "Клиент", "Телефон", "Позиций", "Сумма", "Статус", "Создан"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Order order : facade.getAllOrders()) {
            model.addRow(new Object[]{
                    order.getShortId(),
                    order.getClient().getFullName(),
                    order.getClient().getPhone(),
                    order.getFurnitureList().size(),
                    order.getTotalPrice(),
                    order.getState().getName(),
                    order.getCreatedAtText()
            });
        }
        orderTable.setModel(model);
    }

    private void saveData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("furnistyle.dat"));
        int result = fileChooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            facade.saveToFile(fileChooser.getSelectedFile());
            JOptionPane.showMessageDialog(this, "Данные сохранены.");
        } catch (Exception exception) {
            showError("Не удалось сохранить данные: " + exception.getMessage());
        }
    }

    private void loadData() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            facade.loadFromFile(fileChooser.getSelectedFile());
            JOptionPane.showMessageDialog(this, "Данные загружены.");
        } catch (Exception exception) {
            showError("Не удалось загрузить данные: " + exception.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
}
