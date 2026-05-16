package com.furnistyle.ui;

import com.furnistyle.facade.FurniStyleFacade;
import com.furnistyle.listener.DataChangeListener;
import com.furnistyle.model.furniture.Furniture;
import com.furnistyle.model.furniture.FurnitureCategory;
import com.furnistyle.model.furniture.FurnitureStatus;
import com.furnistyle.model.order.Order;
import com.furnistyle.model.order.OrderItem;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.swing.JOptionPane.showConfirmDialog;

public class MainFrame extends JFrame implements DataChangeListener {
    private static final Color DARK = new Color(45, 45, 45);
    private static final Color PANEL = new Color(60, 60, 60);
    private static final Color FIELD = new Color(75, 75, 75);
    private static final Color LINE = new Color(110, 110, 110);
    private static final Color TEXT = Color.WHITE;
    private static final Color ROW_ONE = new Color(70, 70, 70);
    private static final Color ROW_TWO = new Color(82, 82, 82);
    private static final Color SELECT = new Color(115, 115, 115);

    private final FurniStyleFacade facade;
    private final JTable furnitureTable;
    private final JTable orderTable;
    private final JTextField furnitureSearchField;
    private final JTextField orderSearchField;
    private final List<Furniture> shownFurniture;
    private final List<Order> shownOrders;

    public MainFrame(FurniStyleFacade facade) {
        super("FurniStyle - мебельный салон");
        this.facade = facade;
        this.furnitureTable = new JTable();
        this.orderTable = new JTable();
        this.furnitureSearchField = createSearchField("Поиск по названию, категории, статусу...");
        this.orderSearchField = createSearchField("Поиск по клиенту, телефону, позициям, статусу...");
        this.shownFurniture = new ArrayList<>();
        this.shownOrders = new ArrayList<>();

        facade.addListener(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 720);
        setMinimumSize(new Dimension(920, 620));
        setLocationRelativeTo(null);
        getContentPane().setBackground(DARK);
        setLayout(new BorderLayout());
        add(createTopPanel(), BorderLayout.NORTH);
        add(createTablesPanel(), BorderLayout.CENTER);
        refreshTables();
    }

    @Override
    public void onDataChanged() {
        refreshTables();
    }

    private JPanel createTopPanel() {
        JPanel panel = createCardPanel(new FlowLayout(FlowLayout.LEFT));
        JButton saveButton = createButton("Сохранить базу данных");
        JButton loadButton = createButton("Загрузить базу данных");

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                saveData();
            }
        });
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                loadData();
            }
        });
        panel.add(saveButton);
        panel.add(loadButton);
        return panel;
    }

    private JSplitPane createTablesPanel() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, createFurniturePanel(), createOrderPanel());
        splitPane.setResizeWeight(0.5);
        splitPane.setOneTouchExpandable(true);
        splitPane.setBackground(DARK);
        splitPane.setForeground(TEXT);
        return splitPane;
    }

    private JPanel createFurniturePanel() {
        JPanel panel = createCardPanel(new BorderLayout(0, 16));
        JPanel buttons = createActionPanel();
        JButton addButton = createButton("Добавить мебель");
        JButton editButton = createButton("Редактировать");
        JButton archiveButton = createButton("В архив");
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        JPanel headerPanel = new JPanel(new BorderLayout());

        JLabel headerLabel = new JLabel("Каталог мебели");
        headerLabel.setForeground(TEXT);
        headerPanel.setBackground(PANEL);
        topPanel.setBackground(PANEL);
        headerPanel.add(headerLabel, BorderLayout.NORTH);
        headerPanel.add(createSearchPanel(furnitureSearchField), BorderLayout.CENTER);
        topPanel.add(headerPanel, BorderLayout.NORTH);

        setTable(furnitureTable);
        furnitureTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bindSearch(furnitureSearchField, new Runnable() {
            @Override
            public void run() {
                refreshFurnitureTable();
            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                showFurnitureDialog(null);
            }
        });
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                editSelectedFurniture();
            }
        });
        archiveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                archiveSelectedFurniture();
            }
        });

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
        JButton editButton = createButton("Редактировать заказ");
        JButton nextButton = createButton("Следующий статус");
        JButton rollbackButton = createButton("Откатить статус");
        JButton cancelButton = createButton("Отменить заказ");
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        JPanel headerPanel = new JPanel(new BorderLayout());

        JLabel headerLabel = new JLabel("Заказы");
        headerLabel.setForeground(TEXT);
        headerPanel.setBackground(PANEL);
        topPanel.setBackground(PANEL);
        headerPanel.add(headerLabel, BorderLayout.NORTH);
        headerPanel.add(createSearchPanel(orderSearchField), BorderLayout.CENTER);
        topPanel.add(headerPanel, BorderLayout.NORTH);

        setTable(orderTable);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bindSearch(orderSearchField, new Runnable() {
            @Override
            public void run() {
                refreshOrderTable();
            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                showOrderDialog(null);
            }
        });
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                editSelectedOrder();
            }
        });
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                moveSelectedOrder();
            }
        });
        rollbackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                rollbackSelectedOrder();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                cancelSelectedOrder();
            }
        });

        buttons.add(addButton);
        buttons.add(editButton);
        buttons.add(nextButton);
        buttons.add(rollbackButton);
        buttons.add(cancelButton);
        topPanel.add(createStyledScrollPane(orderTable), BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createCardPanel(java.awt.LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(PANEL);
        panel.setForeground(TEXT);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(PANEL);
        panel.setForeground(TEXT);
        return panel;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(FIELD);
        button.setForeground(TEXT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return button;
    }

    private JScrollPane createStyledScrollPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(LINE));
        scrollPane.getViewport().setBackground(ROW_ONE);
        return scrollPane;
    }

    private JTextField createTextField(String placeholder) {
        JTextField textField = new JTextField();
        textField.setToolTipText(placeholder);
        textField.setBackground(FIELD);
        textField.setForeground(TEXT);
        textField.setCaretColor(TEXT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
        return textField;
    }

    private JTextField createSearchField(String placeholder) {
        return createTextField(placeholder);
    }

    private JTextField createIntegerField(String placeholder, int columns) {
        JTextField textField = new JTextField(columns);
        textField.setToolTipText(placeholder);
        textField.setBackground(FIELD);
        textField.setForeground(TEXT);
        textField.setCaretColor(TEXT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
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
        JLabel label = new JLabel("Поиск:");
        panel.setBackground(PANEL);
        label.setForeground(TEXT);
        panel.add(label, BorderLayout.WEST);
        panel.add(searchField, BorderLayout.CENTER);
        return panel;
    }


    private void findCategory(JTextField searchField, JComboBox<FurnitureCategory> categoryBox) {
        FurnitureCategory oldCategory = (FurnitureCategory) categoryBox.getSelectedItem();
        String text = searchField.getText().trim().toLowerCase();
        DefaultComboBoxModel<FurnitureCategory> model = new DefaultComboBoxModel<>();

        for (FurnitureCategory category : FurnitureCategory.values()) {
            String code = category.name().toLowerCase();
            String name = category.toString().toLowerCase();
            if (text.isEmpty() || code.contains(text) || name.contains(text)) {
                model.addElement(category);
            }
        }

        categoryBox.setModel(model);
        if (oldCategory != null && hasCategory(model, oldCategory)) {
            categoryBox.setSelectedItem(oldCategory);
        } else {
            if (model.getSize() > 0) {
                categoryBox.setSelectedIndex(0);
            }
        }
    }

    private boolean hasCategory(DefaultComboBoxModel<FurnitureCategory> model, FurnitureCategory category) {
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i) == category) {
                return true;
            }
        }
        return false;
    }


    private void setColors(Component item) {
        if (item instanceof JPanel) {
            item.setBackground(PANEL);
            item.setForeground(TEXT);
        }
        if (item instanceof JLabel) {
            item.setForeground(TEXT);
        }
        if (item instanceof JCheckBox) {
            item.setBackground(PANEL);
            item.setForeground(TEXT);
        }
        if (item instanceof JTextField) {
            item.setBackground(FIELD);
            item.setForeground(TEXT);
        }
        if (item instanceof JComboBox) {
            item.setBackground(FIELD);
            item.setForeground(TEXT);
        }
        if (item instanceof JScrollPane) {
            item.setBackground(PANEL);
            item.setForeground(TEXT);
        }
        if (item instanceof Container) {
            Component[] childList = ((Container) item).getComponents();
            for (Component child : childList) {
                setColors(child);
            }
        }
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

    private void setTable(JTable table) {
        DefaultTableCellRenderer cell = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component item = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setOpaque(true);
                item.setForeground(TEXT);
                if (isSelected) {
                    item.setBackground(SELECT);
                } else {
                    if (row % 2 == 0) {
                        item.setBackground(ROW_ONE);
                    } else {
                        item.setBackground(ROW_TWO);
                    }
                }
                return item;
            }
        };
        DefaultTableCellRenderer head = new DefaultTableCellRenderer();
        head.setHorizontalAlignment(SwingConstants.CENTER);
        head.setOpaque(true);
        head.setBackground(DARK);
        head.setForeground(TEXT);

        table.setDefaultRenderer(Object.class, cell);
        table.setDefaultRenderer(Number.class, cell);
        table.getTableHeader().setDefaultRenderer(head);
        table.getTableHeader().setBackground(DARK);
        table.getTableHeader().setForeground(TEXT);
        table.setBackground(ROW_ONE);
        table.setForeground(TEXT);
        table.setGridColor(LINE);
        table.setRowHeight(28);
        table.setSelectionBackground(SELECT);
        table.setSelectionForeground(TEXT);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(false);
        table.setRowSorter(null);
    }

    private void showFurnitureDialog(Furniture furniture) {
        JTextField nameField = createTextField("Например: Диван Oslo");
        JTextField descriptionField = createTextField("Особенности: цвет, покрытие, форма, ножки и т.д.");
        JTextField priceField = createDecimalField("Только цифры, например: 55900");
        JTextField categorySearchField = createSearchField("Поиск категории...");
        JComboBox<FurnitureCategory> categoryBox = new JComboBox<>(FurnitureCategory.values());
        JComboBox<FurnitureStatus> statusBox = new JComboBox<>(FurnitureStatus.values());

        bindSearch(categorySearchField, new Runnable() {
            @Override
            public void run() {
                findCategory(categorySearchField, categoryBox);
            }
        });

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
        panel.add(new JLabel("Поиск категории:"));
        panel.add(categorySearchField);
        panel.add(new JLabel("Категория:"));
        panel.add(categoryBox);
        panel.add(new JLabel("Стоимость:"));
        panel.add(priceField);
        panel.add(new JLabel("Статус:"));
        panel.add(statusBox);

        setColors(panel);
        int result = showThemedConfirmDialog(this, panel, "Мебель", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            double price = Double.parseDouble(normalizeDecimal(priceField.getText()));
            FurnitureCategory category = (FurnitureCategory) categoryBox.getSelectedItem();
            FurnitureStatus status = (FurnitureStatus) statusBox.getSelectedItem();
            if (category == null) {
                throw new IllegalArgumentException("Выберите категорию мебели.");
            }

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

    private void showOrderDialog(Order order) {
        List<Furniture> furnitureList = getFurnitureForOrderDialog(order);
        if (furnitureList.isEmpty()) {
            showError("Нет доступной мебели для заказа.");
            return;
        }

        JTextField clientNameField = createTextField("ФИО клиента");
        JTextField phoneField = createPhoneField();
        Map<Furniture, Integer> countMap = getSelectedQuantities(order);
        if (order != null) {
            clientNameField.setText(order.getClient().getFullName());
            phoneField.setText(order.getClient().getPhone());
        }
        JTextField furnitureSearch = createSearchField("Поиск мебели для заказа...");
        JPanel furniturePanel = new JPanel();
        furniturePanel.setLayout(new BoxLayout(furniturePanel, BoxLayout.Y_AXIS));
        List<JCheckBox> checkBoxes = new ArrayList<>();
        List<JTextField> quantityFields = new ArrayList<>();
        List<JPanel> itemRows = new ArrayList<>();

        for (Furniture furniture : furnitureList) {
            JCheckBox checkBox = new JCheckBox(getFurnitureSelectionText(furniture));
            JTextField quantityField = createIntegerField("Кол-во", 4);
            JPanel itemRow = new JPanel(new BorderLayout(12, 0));
            JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            JLabel quantityLabel = new JLabel("Кол-во:");

            quantityField.setText(String.valueOf(countMap.getOrDefault(furniture, 1)));
            checkBox.setSelected(countMap.containsKey(furniture));
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

        bindSearch(furnitureSearch, new Runnable() {
            @Override
            public void run() {
                filterOrderDialogFurniture(furnitureSearch, furnitureList, itemRows, furniturePanel);
            }
        });

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

        String dialogName;
        String saveText;
        if (order == null) {
            dialogName = "Новый заказ";
            saveText = "Создать";
        } else {
            dialogName = "Редактирование заказа";
            saveText = "Сохранить";
        }

        JDialog dialog = new JDialog(this, dialogName, true);
        JPanel dialogButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton createButton = createButton(saveText);
        JButton cancelButton = createButton("Отмена");

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                List<OrderItem> itemList = new ArrayList<>();
                try {
                    for (int i = 0; i < checkBoxes.size(); i++) {
                        if (checkBoxes.get(i).isSelected()) {
                            int count = Integer.parseInt(quantityFields.get(i).getText().trim());
                            itemList.add(new OrderItem(furnitureList.get(i), count));
                        }
                    }
                    if (!isPhoneComplete(phoneField.getText())) {
                        throw new IllegalArgumentException("Введите телефон в международном формате: плюс и от 4 до 15 цифр.");
                    }
                    if (order == null) {
                        facade.createOrder(clientNameField.getText().trim(), phoneField.getText().trim(), itemList);
                    } else {
                        facade.updateOrder(order, clientNameField.getText().trim(), phoneField.getText().trim(), itemList);
                    }
                    dialog.dispose();
                } catch (NumberFormatException exception) {
                    showDialogError(dialog, "Количество должно быть целым числом.");
                } catch (RuntimeException exception) {
                    showDialogError(dialog, exception.getMessage());
                }
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                dialog.dispose();
            }
        });
        dialogButtons.add(createButton);
        dialogButtons.add(cancelButton);

        setColors(panel);
        setColors(dialogButtons);
        dialog.getContentPane().setBackground(PANEL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(dialogButtons, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


    private List<Furniture> getFurnitureForOrderDialog(Order order) {
        List<Furniture> furniture = new ArrayList<>();
        if (order != null) {
            for (OrderItem item : order.getOrderItems()) {
                if (!furniture.contains(item.getFurniture())) {
                    furniture.add(item.getFurniture());
                }
            }
        }
        for (Furniture item : facade.getOrderAvailableFurniture()) {
            if (!furniture.contains(item)) {
                furniture.add(item);
            }
        }
        return furniture;
    }

    private Map<Furniture, Integer> getSelectedQuantities(Order order) {
        Map<Furniture, Integer> countMap = new HashMap<>();
        if (order == null) {
            return countMap;
        }
        for (OrderItem item : order.getOrderItems()) {
            countMap.put(item.getFurniture(), item.getQuantity());
        }
        return countMap;
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
        if (!confirmDo("Переместить мебель в архив?")) {
            return;
        }
        facade.archiveFurniture(furniture);
    }


    private void editSelectedOrder() {
        Order order = getSelectedOrder();
        if (order == null) {
            showError("Выберите заказ в таблице.");
            return;
        }
        showOrderDialog(order);
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
        if (!confirmDo("Отменить выбранный заказ?")) {
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
        return shownFurniture.get(row);
    }

    private Order getSelectedOrder() {
        int row = orderTable.getSelectedRow();
        if (row < 0) {
            return null;
        }
        return shownOrders.get(row);
    }

    private void refreshTables() {
        refreshFurnitureTable();
        refreshOrderTable();
    }

    private void refreshFurnitureTable() {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Название", "Описание", "Тип", "Категория", "Стоимость", "Статус"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        String query = furnitureSearchField.getText().trim().toLowerCase();

        shownFurniture.clear();
        for (Furniture furniture : facade.getAllFurniture()) {
            if (!matchesFurniture(furniture, query)) {
                continue;
            }
            shownFurniture.add(furniture);
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

        shownOrders.clear();
        for (Order order : facade.getAllOrders()) {
            if (!matchesOrder(order, query)) {
                continue;
            }
            shownOrders.add(order);
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
        for (int row = 0; row < shownOrders.size(); row++) {
            int itemCount = Math.max(1, shownOrders.get(row).getOrderItems().size());
            orderTable.setRowHeight(row, Math.max(34, itemCount * 20 + 14));
        }
    }

    private String getOrderItemsHtml(Order order) {
        StringBuilder text = new StringBuilder("<html>");
        for (OrderItem item : order.getOrderItems()) {
            if (text.length() > "<html>".length()) {
                text.append("<br>");
            }
            text.append(escapeHtml(item.getDescription()));
        }
        text.append("</html>");
        return text.toString();
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

    private boolean confirmDo(String message) {
        return showThemedConfirmDialog(this, message, "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private void showError(String message) {
        showDialogError(this, message);
    }

    private void showDialogError(Component parent, String message) {
        showThemedMessageDialog(parent, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
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
            String addText = "";
            if (text != null) {
                addText = text;
            }
            String newText = currentText.substring(0, offset) + addText + currentText.substring(offset + length);
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
            String addText = "";
            if (text != null) {
                addText = text;
            }
            String candidate = currentText.substring(0, offset) + addText + currentText.substring(offset + length);
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

    private void styleDialog(Window dialog) {
        dialog.setBackground(PANEL);
        setColors(dialog);
        if (dialog instanceof JDialog) {
            ((JDialog) dialog).getContentPane().setBackground(PANEL);
        }
    }

    private int showThemedConfirmDialog(Component parent, Object message, String title, int optionType) {
        JOptionPane pane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE, optionType);
        JDialog dialog = pane.createDialog(parent, title);
        styleDialog(dialog);
        dialog.setVisible(true);
        Object selectedValue = pane.getValue();
        if (selectedValue == null) return JOptionPane.CLOSED_OPTION;
        if (selectedValue instanceof Integer) return (Integer) selectedValue;
        return JOptionPane.CLOSED_OPTION;
    }

    private void showThemedMessageDialog(Component parent, Object message, String title, int messageType) {
        JOptionPane pane = new JOptionPane(message, messageType);
        JDialog dialog = pane.createDialog(parent, title);
        styleDialog(dialog);
        dialog.setVisible(true);
    }
}
