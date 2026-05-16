package com.furnistyle.ui;

import com.furnistyle.facade.FurniStyleFacade;
import com.furnistyle.listener.DataChangeListener;
import com.furnistyle.model.furniture.Furniture;
import com.furnistyle.model.furniture.FurnitureCategory;
import com.furnistyle.model.furniture.FurnitureStatus;
import com.furnistyle.model.order.Order;
import com.furnistyle.model.order.OrderItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
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
    private static final Color APP_BACKGROUND = new Color(238, 240, 243);
    private static final Color CARD_BACKGROUND = new Color(250, 251, 252);
    private static final Color SURFACE_BACKGROUND = new Color(245, 246, 248);
    private static final Color BORDER_COLOR = new Color(214, 218, 224);
    private static final Color TEXT_PRIMARY = new Color(45, 49, 54);
    private static final Color TEXT_MUTED = new Color(105, 113, 123);
    private static final Color PROMPT_TEXT = new Color(150, 156, 166);
    private static final Color BUTTON_BACKGROUND = new Color(72, 78, 87);
    private static final Color BUTTON_HOVER = new Color(93, 101, 112);
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 22);
    private static final Font UI_FONT = new Font("SansSerif", Font.PLAIN, 14);

    static {
        installModernLookAndFeel();
    }

    private final JTable furnitureTable;
    private final JTable orderTable;
    private final JLabel summaryLabel;
    private final JTextField furnitureSearchField;
    private final JTextField orderSearchField;
    private final List<Furniture> displayedFurniture;
    private final List<Order> displayedOrders;
    private final FurniStyleFacade facade;

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
        getContentPane().setBackground(APP_BACKGROUND);
        setLayout(new BorderLayout(18, 18));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(18, 22, 18, 22));
        add(createTopPanel(), BorderLayout.NORTH);
        add(createTabs(), BorderLayout.CENTER);
        add(createSummaryPanel(), BorderLayout.SOUTH);
        refreshTables();
    }

    private static void installModernLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            UIManager.put("Panel.background", APP_BACKGROUND);
        }
        UIManager.put("OptionPane.background", CARD_BACKGROUND);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
        UIManager.put("Panel.background", CARD_BACKGROUND);
        UIManager.put("Label.foreground", TEXT_PRIMARY);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.foreground", TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground", TEXT_PRIMARY);
        UIManager.put("ComboBox.background", Color.WHITE);
        UIManager.put("ComboBox.foreground", TEXT_PRIMARY);
    }

    @Override
    public void onDataChanged() {
        refreshTables();
    }

    private JPanel createTopPanel() {
        JPanel panel = createCardPanel(new BorderLayout(16, 0));
        JLabel title = new JLabel("FurniStyle");
        JLabel subtitle = new JLabel("Современная информационная система мебельного салона");
        JPanel titlePanel = new JPanel(new GridLayout(0, 1, 0, 4));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton saveButton = createModernButton("Сохранить");
        JButton loadButton = createModernButton("Загрузить");

        title.setFont(TITLE_FONT);
        title.setForeground(TEXT_PRIMARY);
        subtitle.setFont(UI_FONT);
        subtitle.setForeground(TEXT_MUTED);
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(subtitle);

        actions.setOpaque(false);
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
        tabs.setFont(UI_FONT.deriveFont(Font.BOLD));
        tabs.setBackground(APP_BACKGROUND);
        tabs.setForeground(TEXT_PRIMARY);
        tabs.addTab("Каталог мебели", createFurniturePanel());
        tabs.addTab("Заказы", createOrderPanel());
        return tabs;
    }

    private JPanel createFurniturePanel() {
        JPanel panel = createCardPanel(new BorderLayout(0, 16));
        JPanel buttons = createActionPanel();
        JButton addButton = createModernButton("Добавить мебель");
        JButton editButton = createSecondaryButton("Редактировать");
        JButton archiveButton = createSecondaryButton("В архив");
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));

        topPanel.setOpaque(false);
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
        JButton addButton = createModernButton("Создать заказ");
        JButton nextButton = createSecondaryButton("Следующий статус");
        JButton rollbackButton = createSecondaryButton("Откатить статус");
        JButton cancelButton = createSecondaryButton("Отменить заказ");
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));

        topPanel.setOpaque(false);
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
        JPanel panel = new JPanel(layout);
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(18, 20, 18, 20)
        ));
        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setOpaque(false);
        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = createCardPanel(new BorderLayout());
        summaryLabel.setFont(UI_FONT.deriveFont(Font.BOLD));
        summaryLabel.setForeground(TEXT_MUTED);
        panel.add(summaryLabel, BorderLayout.WEST);
        return panel;
    }

    private JButton createModernButton(String text) {
        JButton button = createBaseButton(text);
        button.setBackground(BUTTON_BACKGROUND);
        button.setForeground(Color.WHITE);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent event) {
                button.setBackground(BUTTON_HOVER);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent event) {
                button.setBackground(BUTTON_BACKGROUND);
            }
        });
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = createBaseButton(text);
        button.setBackground(SURFACE_BACKGROUND);
        button.setForeground(TEXT_PRIMARY);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(9, 16, 9, 16)
        ));
        button.setBorderPainted(true);
        return button;
    }

    private JButton createBaseButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(UI_FONT.deriveFont(Font.BOLD));
        button.setBorder(new EmptyBorder(10, 18, 10, 18));
        return button;
    }

    private JScrollPane createStyledScrollPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    private JTextField createTextField(String placeholder) {
        JTextField textField = new PromptTextField(placeholder);
        textField.setFont(UI_FONT);
        textField.setToolTipText(placeholder);
        return textField;
    }

    private JTextField createSearchField(String placeholder) {
        return createTextField(placeholder);
    }

    private JTextField createIntegerField(String placeholder, int columns) {
        JTextField textField = new PromptTextField(placeholder, columns);
        textField.setFont(UI_FONT);
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
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        JLabel label = new JLabel("Поиск:");

        panel.setOpaque(false);
        label.setFont(UI_FONT.deriveFont(Font.BOLD));
        label.setForeground(TEXT_MUTED);
        panel.add(label, BorderLayout.WEST);
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
        table.setFont(UI_FONT);
        table.setForeground(TEXT_PRIMARY);
        table.setBackground(Color.WHITE);
        table.setGridColor(new Color(229, 232, 236));
        table.setRowHeight(34);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(219, 224, 230));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setAutoCreateRowSorter(false);
        table.setRowSorter(null);

        JTableHeader header = table.getTableHeader();
        header.setFont(UI_FONT.deriveFont(Font.BOLD));
        header.setBackground(new Color(230, 233, 237));
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        header.setReorderingAllowed(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                component.setFont(UI_FONT);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                if (!isSelected) {
                    component.setBackground(row % 2 == 0 ? Color.WHITE : SURFACE_BACKGROUND);
                    component.setForeground(TEXT_PRIMARY);
                }
                return component;
            }
        });
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
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
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
        furniturePanel.setBackground(CARD_BACKGROUND);
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
            checkBox.setBackground(CARD_BACKGROUND);
            checkBox.setFont(UI_FONT);
            checkBox.setForeground(TEXT_PRIMARY);
            checkBox.setVerticalAlignment(JCheckBox.CENTER);
            quantityLabel.setFont(UI_FONT);
            quantityLabel.setForeground(TEXT_MUTED);
            quantityPanel.setOpaque(false);
            quantityPanel.add(quantityLabel);
            quantityPanel.add(quantityField);
            itemRow.setOpaque(false);
            itemRow.setBorder(new EmptyBorder(4, 0, 4, 0));
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
        panel.setBackground(CARD_BACKGROUND);
        JPanel clientPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        clientPanel.setBackground(CARD_BACKGROUND);
        clientPanel.add(new JLabel("Клиент:"));
        clientPanel.add(clientNameField);
        clientPanel.add(new JLabel("Телефон:"));
        clientPanel.add(phoneField);
        JPanel orderItemsPanel = new JPanel(new BorderLayout(0, 8));
        JScrollPane orderItemsScrollPane = new JScrollPane(furniturePanel);

        orderItemsPanel.setOpaque(false);
        orderItemsScrollPane.setPreferredSize(new Dimension(760, 320));
        orderItemsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        orderItemsScrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        orderItemsPanel.add(createSearchPanel(furnitureSearch), BorderLayout.NORTH);
        orderItemsPanel.add(orderItemsScrollPane, BorderLayout.CENTER);
        panel.add(clientPanel, BorderLayout.NORTH);
        panel.add(orderItemsPanel, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

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

        int confirm = JOptionPane.showConfirmDialog(this,
                "Отправить мебель \"" + furniture.getName() + "\" в архив?",
                "Подтверждение архивации",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            facade.archiveFurniture(furniture);
        }
    }

    private void moveSelectedOrder() {
        Order order = getSelectedOrder();
        if (order == null) {
            showError("Выберите заказ в таблице.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Перевести заказ №" + order.getShortId() + " в следующий статус?",
                "Подтверждение действия",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                facade.moveOrderToNextState(order);
            } catch (RuntimeException exception) {
                showError(exception.getMessage());
            }
        }
    }

    private void rollbackSelectedOrder() {
        Order order = getSelectedOrder();
        if (order == null) {
            showError("Выберите заказ в таблице.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Откатить статус заказа №" + order.getShortId() + " к предыдущему?",
                "Подтверждение действия",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                facade.rollbackOrderState(order);
            } catch (RuntimeException exception) {
                showError(exception.getMessage());
            }
        }
    }

    private void cancelSelectedOrder() {
        Order order = getSelectedOrder();
        if (order == null) {
            showError("Выберите заказ в таблице.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Отменить заказ №" + order.getShortId() + "? Это действие необратимо.",
                "Подтверждение отмены",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                facade.cancelOrder(order);
            } catch (RuntimeException exception) {
                showError(exception.getMessage());
            }
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

    private static class PromptTextField extends JTextField {
        private final String prompt;

        PromptTextField(String prompt) {
            this(prompt, 0);
        }

        PromptTextField(String prompt, int columns) {
            super(columns);
            this.prompt = prompt;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            if (!getText().isEmpty()) {
                return;
            }

            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setColor(PROMPT_TEXT);
            graphics2D.setFont(getFont().deriveFont(Font.ITALIC));
            int leftInset = getInsets().left;
            int top = (getHeight() - getFontMetrics(getFont()).getHeight()) / 2 + getFontMetrics(getFont()).getAscent();
            graphics2D.drawString(prompt, leftInset + 2, top);
            graphics2D.dispose();
        }
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
