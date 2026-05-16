package com.furnistyle.ui;

import com.furnistyle.facade.FurniStyleFacade;
import com.furnistyle.listener.DataChangeListener;
import com.furnistyle.model.furniture.Furniture;
import com.furnistyle.model.furniture.FurnitureCategory;
import com.furnistyle.model.furniture.FurnitureStatus;
import com.furnistyle.model.order.Order;
import com.furnistyle.model.order.OrderItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainFrame extends JFrame implements DataChangeListener {
    private final FurniStyleFacade facade;
    private final JTable furnitureTable;
    private final JTable orderTable;
    private final JLabel summaryLabel;
    private final JTextField furnitureSearchField;
    private final JTextField orderSearchField;
    private final List<Furniture> displayedFurniture;
    private final List<Order> displayedOrders;

    public MainFrame(FurniStyleFacade facade) {
        super("FurniStyle - мебельный салон");
        this.facade = facade;
        this.furnitureTable = new JTable();
        this.orderTable = new JTable();
        this.summaryLabel = new JLabel();
        this.furnitureSearchField = createSearchField("Поиск по названию, категории, статусу...");
        this.orderSearchField = createSearchField("Поиск по клиенту, телефону, позициям, статусу...");
        this.displayedFurniture = new ArrayList<>();
        this.displayedOrders = new ArrayList<>();

        facade.addListener(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 720);
        setMinimumSize(new Dimension(920, 620));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(createTopPanel(), BorderLayout.NORTH);
        add(createTabs(), BorderLayout.CENTER);
        add(createSummaryPanel(), BorderLayout.SOUTH);
        refreshTables();
    }

    @Override
    public void onDataChanged() {
        refreshTables();
    }

    private JPanel createTopPanel() {
        JPanel panel = createCardPanel(new BorderLayout());
        JLabel title = new JLabel("FurniStyle");
        JLabel subtitle = new JLabel("Информационная система мебельного салона");
        JPanel titlePanel = new JPanel(new GridLayout(0, 1));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = createButton("Сохранить");
        JButton loadButton = createButton("Загрузить");

        titlePanel.add(title);
        titlePanel.add(subtitle);

        saveButton.addActionListener(event -> saveData());
        loadButton.addActionListener(event -> loadData());
        actions.add(saveButton);
        actions.add(loadButton);

        panel.add(titlePanel, BorderLayout.WEST);
        panel.add(actions, BorderLayout.EAST);
        return panel;
    }

    private JTabbedPane createTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Каталог мебели", createFurniturePanel());
        tabs.addTab("Заказы", createOrderPanel());
        return tabs;
    }

    private JPanel createFurniturePanel() {
        JPanel panel = createCardPanel(new BorderLayout(0, 16));
        JPanel buttons = createActionPanel();
        JButton addButton = createButton("Добавить мебель");
        JButton editButton = createButton("Редактировать");
        JButton archiveButton = createButton("В архив");
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));

        topPanel.add(createSearchPanel(furnitureSearchField), BorderLayout.NORTH);

        configureTable(furnitureTable);
        furnitureTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bindSearch(furnitureSearchField, this::refreshFurnitureTable);
        addButton.addActionListener(event -> showFurnitureDialog(null));
        editButton.addActionListener(event -> editSelectedFurniture());
        archiveButton.addActionListener(event -> archiveSelectedFurniture());

        buttons.add(addButton);
        buttons.add(editButton);
        buttons.add(archiveButton);
        topPanel.add(createStyledScrollPane(furnitureTable), BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createOrderPanel() {
        JPanel panel = createCardPanel(new BorderLayout(0, 16));
        JPanel buttons = createActionPanel();
        JButton addButton = createButton("Создать заказ");
        JButton nextButton = createButton("Следующий статус");
        JButton rollbackButton = createButton("Откатить статус");
        JButton cancelButton = createButton("Отменить заказ");
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));

        topPanel.add(createSearchPanel(orderSearchField), BorderLayout.NORTH);

        configureTable(orderTable);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bindSearch(orderSearchField, this::refreshOrderTable);
        addButton.addActionListener(event -> showOrderDialog());
        nextButton.addActionListener(event -> moveSelectedOrder());
        rollbackButton.addActionListener(event -> rollbackSelectedOrder());
        cancelButton.addActionListener(event -> cancelSelectedOrder());

        buttons.add(addButton);
        buttons.add(nextButton);
        buttons.add(rollbackButton);
        buttons.add(cancelButton);
        topPanel.add(createStyledScrollPane(orderTable), BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createCardPanel(java.awt.LayoutManager layout) {
        return new JPanel(layout);
    }

    private JPanel createActionPanel() {
        return new JPanel(new FlowLayout(FlowLayout.RIGHT));
    }

    private JPanel createSummaryPanel() {
        JPanel panel = createCardPanel(new BorderLayout());
        panel.add(summaryLabel, BorderLayout.WEST);
        return panel;
    }

    private JButton createButton(String text) {
        return new JButton(text);
    }

    private JScrollPane createStyledScrollPane(JTable table) {
        return new JScrollPane(table);
    }

    private JTextField createTextField(String placeholder) {
        JTextField textField = new JTextField();
        textField.setToolTipText(placeholder);
        return textField;
    }

    private JTextField createSearchField(String placeholder) {
        return createTextField(placeholder);
    }

    private JTextField createIntegerField(String placeholder, int columns) {
        JTextField textField = new JTextField(columns);
        textField.setToolTipText(placeholder);
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new NumericDocumentFilter(false));
        return textField;
    }

    private JTextField createDecimalField(String placeholder) {
        JTextField textField = createTextField(placeholder);
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new NumericDocumentFilter(true));
        return textField;
    }

    private JTextField createPhoneField() {
        JTextField phoneField = createTextField("+ и 4–15 цифр, например: +4915112345678");
        ((AbstractDocument) phoneField.getDocument()).setDocumentFilter(new PhoneDocumentFilter());
        return phoneField;
    }

    private JPanel createSearchPanel(JTextField searchField) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Поиск:"), BorderLayout.WEST);
        panel.add(searchField, BorderLayout.CENTER);
        return panel;
    }

    private void bindSearch(JTextField searchField, Runnable refreshAction) {
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent event) {
                refreshAction.run();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent event) {
                refreshAction.run();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent event) {
                refreshAction.run();
            }
        });
    }

    private void configureTable(JTable table) {
        table.setAutoCreateRowSorter(false);
        table.setRowSorter(null);
    }

    private void showFurnitureDialog(Furniture furniture) {
        JTextField nameField = createTextField("Например: Диван Oslo");
        JTextField descriptionField = createTextField("Особенности: цвет, покрытие, форма, ножки и т.д.");
        JTextField priceField = createDecimalField("Только цифры, например: 55900");
        JComboBox<FurnitureCategory> categoryBox = new JComboBox<>(FurnitureCategory.values());
        JComboBox<FurnitureStatus> statusBox = new JComboBox<>(FurnitureStatus.values());


        if (furniture != null) {
            nameField.setText(furniture.getName());
            descriptionField.setText(furniture.getDescription());
            priceField.setText(String.valueOf(furniture.getPrice()));
            categoryBox.setSelectedItem(furniture.getCategory());
            statusBox.setSelectedItem(furniture.getStatus());
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Название:"));
        panel.add(nameField);
        panel.add(new JLabel("Описание:"));
        panel.add(descriptionField);
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
            double price = Double.parseDouble(normalizeDecimal(priceField.getText()));
            FurnitureCategory category = (FurnitureCategory) categoryBox.getSelectedItem();
            FurnitureStatus status = (FurnitureStatus) statusBox.getSelectedItem();

            if (furniture == null) {
                facade.addFurniture(nameField.getText().trim(), category, price, status, descriptionField.getText().trim());
            } else {
                facade.updateFurniture(furniture, nameField.getText().trim(), category, price, status, descriptionField.getText().trim());
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

        JTextField clientNameField = createTextField("ФИО клиента");
        JTextField phoneField = createPhoneField();
        JTextField furnitureSearch = createSearchField("Поиск мебели для заказа...");
        JPanel furniturePanel = new JPanel();
        furniturePanel.setLayout(new BoxLayout(furniturePanel, BoxLayout.Y_AXIS));
        List<JCheckBox> checkBoxes = new ArrayList<>();
        List<JTextField> quantityFields = new ArrayList<>();
        List<JPanel> itemRows = new ArrayList<>();

        for (Furniture furniture : availableFurniture) {
            JCheckBox checkBox = new JCheckBox(getFurnitureSelectionText(furniture));
            JTextField quantityField = createIntegerField("Кол-во", 4);
            JPanel itemRow = new JPanel(new BorderLayout(12, 0));
            JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            JLabel quantityLabel = new JLabel("Кол-во:");

            quantityField.setText("1");
            checkBox.setVerticalAlignment(JCheckBox.CENTER);
            quantityPanel.add(quantityLabel);
            quantityPanel.add(quantityField);
            itemRow.setPreferredSize(new Dimension(720, 42));
            itemRow.setMinimumSize(new Dimension(0, 42));
            itemRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            itemRow.add(checkBox, BorderLayout.CENTER);
            itemRow.add(quantityPanel, BorderLayout.EAST);
            checkBoxes.add(checkBox);
            quantityFields.add(quantityField);
            itemRows.add(itemRow);
            furniturePanel.add(itemRow);
        }

        bindSearch(furnitureSearch, () -> filterOrderDialogFurniture(furnitureSearch, availableFurniture, itemRows, furniturePanel));

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel clientPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        clientPanel.add(new JLabel("Клиент:"));
        clientPanel.add(clientNameField);
        clientPanel.add(new JLabel("Телефон:"));
        clientPanel.add(phoneField);
        JPanel orderItemsPanel = new JPanel(new BorderLayout(0, 8));
        JScrollPane orderItemsScrollPane = new JScrollPane(furniturePanel);

        orderItemsScrollPane.setPreferredSize(new Dimension(760, 320));
        orderItemsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        orderItemsPanel.add(createSearchPanel(furnitureSearch), BorderLayout.NORTH);
        orderItemsPanel.add(orderItemsScrollPane, BorderLayout.CENTER);
        panel.add(clientPanel, BorderLayout.NORTH);
        panel.add(orderItemsPanel, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, panel, "Новый заказ", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        List<OrderItem> selectedItems = new ArrayList<>();
        try {
            for (int i = 0; i < checkBoxes.size(); i++) {
                if (checkBoxes.get(i).isSelected()) {
                    int quantity = Integer.parseInt(quantityFields.get(i).getText().trim());
                    selectedItems.add(new OrderItem(availableFurniture.get(i), quantity));
                }
            }
            if (!isPhoneComplete(phoneField.getText())) {
                throw new IllegalArgumentException("Введите телефон в международном формате: плюс и от 4 до 15 цифр.");
            }
            facade.createOrder(clientNameField.getText().trim(), phoneField.getText().trim(), selectedItems);
        } catch (NumberFormatException exception) {
            showError("Количество должно быть целым числом.");
        } catch (RuntimeException exception) {
            showError(exception.getMessage());
        }
    }

    private String getFurnitureSelectionText(Furniture furniture) {
        String description = furniture.getDescription();
        String baseText = furniture.getName() + " - " + furniture.getPrice() + " руб. (" + furniture.getStatus() + ")";
        if (description.isBlank()) {
            return baseText;
        }
        return baseText + " — " + description;
    }

    private void filterOrderDialogFurniture(JTextField searchField, List<Furniture> furnitureList,
                                            List<JPanel> itemRows, JPanel furniturePanel) {
        String query = searchField.getText().trim().toLowerCase();
        furniturePanel.removeAll();
        for (int i = 0; i < furnitureList.size(); i++) {
            Furniture furniture = furnitureList.get(i);
            String searchableText = (furniture.getName() + " " + furniture.getDescription() + " "
                    + furniture.getCategory() + " " + furniture.getTypeDescription() + " "
                    + furniture.getStatus()).toLowerCase();
            if (query.isEmpty() || searchableText.contains(query)) {
                furniturePanel.add(itemRows.get(i));
            }
        }
        furniturePanel.revalidate();
        furniturePanel.repaint();
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

    private void rollbackSelectedOrder() {
        Order order = getSelectedOrder();
        if (order == null) {
            showError("Выберите заказ в таблице.");
            return;
        }

        try {
            facade.rollbackOrderState(order);
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
        return displayedFurniture.get(row);
    }

    private Order getSelectedOrder() {
        int row = orderTable.getSelectedRow();
        if (row < 0) {
            return null;
        }
        return displayedOrders.get(row);
    }

    private void refreshTables() {
        refreshFurnitureTable();
        refreshOrderTable();
        summaryLabel.setText("Мебели в каталоге: " + facade.getAllFurniture().size() + "   •   Заказов: " + facade.getAllOrders().size());
    }

    private void refreshFurnitureTable() {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Название", "Описание", "Тип", "Категория", "Стоимость", "Статус"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        String query = furnitureSearchField.getText().trim().toLowerCase();

        displayedFurniture.clear();
        for (Furniture furniture : facade.getAllFurniture()) {
            if (!matchesFurniture(furniture, query)) {
                continue;
            }
            displayedFurniture.add(furniture);
            model.addRow(new Object[]{
                    furniture.getName(),
                    furniture.getDescription(),
                    furniture.getTypeDescription(),
                    furniture.getCategory(),
                    furniture.getPrice(),
                    furniture.getStatus()
            });
        }
        furnitureTable.setModel(model);
    }

    private void refreshOrderTable() {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"№", "Клиент", "Телефон", "Позиции", "Кол-во", "Сумма", "Статус", "Создан"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        String query = orderSearchField.getText().trim().toLowerCase();

        displayedOrders.clear();
        for (Order order : facade.getAllOrders()) {
            if (!matchesOrder(order, query)) {
                continue;
            }
            displayedOrders.add(order);
            model.addRow(new Object[]{
                    order.getShortId(),
                    order.getClient().getFullName(),
                    order.getClient().getPhone(),
                    getOrderItemsHtml(order),
                    order.getTotalQuantity(),
                    order.getTotalPrice(),
                    order.getState().getName(),
                    order.getCreatedAtText()
            });
        }
        orderTable.setModel(model);
        updateOrderTableRowHeights();
    }

    private void updateOrderTableRowHeights() {
        for (int row = 0; row < displayedOrders.size(); row++) {
            int itemCount = Math.max(1, displayedOrders.get(row).getOrderItems().size());
            orderTable.setRowHeight(row, Math.max(34, itemCount * 20 + 14));
        }
    }

    private String getOrderItemsHtml(Order order) {
        return "<html>" + order.getOrderItems().stream()
                .map(OrderItem::getDescription)
                .map(this::escapeHtml)
                .collect(Collectors.joining("<br>")) + "</html>";
    }

    private String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private boolean matchesFurniture(Furniture furniture, String query) {
        if (query.isEmpty()) {
            return true;
        }
        String searchableText = (furniture.getName() + " " + furniture.getDescription() + " "
                + furniture.getTypeDescription() + " " + furniture.getCategory() + " "
                + furniture.getPrice() + " " + furniture.getStatus()).toLowerCase();
        return searchableText.contains(query);
    }

    private boolean matchesOrder(Order order, String query) {
        if (query.isEmpty()) {
            return true;
        }
        String searchableText = (order.getShortId() + " " + order.getClient().getFullName() + " "
                + order.getClient().getPhone() + " " + order.getItemsText() + " "
                + order.getTotalQuantity() + " " + order.getTotalPrice() + " "
                + order.getState().getName() + " " + order.getCreatedAtText()).toLowerCase();
        return searchableText.contains(query);
    }

    private String normalizeDecimal(String value) {
        return value.trim().replace(',', '.');
    }

    private boolean isPhoneComplete(String value) {
        if (value == null || !value.startsWith("+")) {
            return false;
        }
        int digitCount = value.replaceAll("\\D", "").length();
        return digitCount >= PhoneDocumentFilter.MIN_PHONE_DIGITS
                && digitCount <= PhoneDocumentFilter.MAX_PHONE_DIGITS;
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


    private static class NumericDocumentFilter extends DocumentFilter {
        private final boolean decimalAllowed;

        NumericDocumentFilter(boolean decimalAllowed) {
            this.decimalAllowed = decimalAllowed;
        }

        @Override
        public void insertString(FilterBypass filterBypass, int offset, String string, AttributeSet attributeSet) throws BadLocationException {
            replace(filterBypass, offset, 0, string, attributeSet);
        }

        @Override
        public void replace(FilterBypass filterBypass, int offset, int length, String text, AttributeSet attributeSet) throws BadLocationException {
            String currentText = filterBypass.getDocument().getText(0, filterBypass.getDocument().getLength());
            String newText = currentText.substring(0, offset) + (text == null ? "" : text) + currentText.substring(offset + length);
            if (isValid(newText)) {
                super.replace(filterBypass, offset, length, text, attributeSet);
            }
        }

        private boolean isValid(String value) {
            if (value.isEmpty()) {
                return true;
            }
            if (!decimalAllowed) {
                return value.matches("\\d+");
            }
            return value.matches("\\d*([.,]\\d*)?");
        }
    }

    private static class PhoneDocumentFilter extends DocumentFilter {
        private static final int MIN_PHONE_DIGITS = 4;
        private static final int MAX_PHONE_DIGITS = 15;

        @Override
        public void insertString(FilterBypass filterBypass, int offset, String string, AttributeSet attributeSet) throws BadLocationException {
            replace(filterBypass, offset, 0, string, attributeSet);
        }

        @Override
        public void replace(FilterBypass filterBypass, int offset, int length, String text, AttributeSet attributeSet) throws BadLocationException {
            String currentText = filterBypass.getDocument().getText(0, filterBypass.getDocument().getLength());
            String candidate = currentText.substring(0, offset) + (text == null ? "" : text) + currentText.substring(offset + length);
            if (isValid(candidate)) {
                super.replace(filterBypass, offset, length, text, attributeSet);
            }
        }

        private boolean isValid(String value) {
            if (value.isEmpty() || value.equals("+")) {
                return true;
            }
            if (!value.startsWith("+")) {
                return false;
            }
            String phoneBody = value.substring(1);
            int digitCount = phoneBody.length();
            return digitCount <= MAX_PHONE_DIGITS && phoneBody.matches("\\d*");
        }
    }
}
