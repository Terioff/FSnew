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

import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;

public class MainFrame extends JFrame implements DataChangeListener {
    private static final Color APP_BACKGROUND = new Color(238, 240, 243);
    private static final Color CARD_BACKGROUND = new Color(250, 251, 252);
    private static final Color SURFACE_BACKGROUND = new Color(245, 246, 248);
    private static final Color BORDER_COLOR = new Color(214, 218, 224);
    private static final Color TEXT_PRIMARY = new Color(45, 49, 54);
    private static final Color TEXT_MUTED = new Color(105, 113, 123);
    private static final Color BUTTON_BACKGROUND = new Color(72, 78, 87);
    private static final Color BUTTON_HOVER = new Color(93, 101, 112);
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 22);
    private static final Font UI_FONT = new Font("SansSerif", Font.PLAIN, 14);
    static {
        installModernLookAndFeel();
    }

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
        setSize(1080, 720);
        setMinimumSize(new java.awt.Dimension(920, 620));
        setSize(1000, 650);
        setLocationRelativeTo(null);
        getContentPane().setBackground(APP_BACKGROUND);
        setLayout(new BorderLayout(18, 18));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(18, 22, 18, 22));
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

        configureTable(furnitureTable);
        furnitureTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        addButton.addActionListener(event -> showFurnitureDialog(null));
        editButton.addActionListener(event -> editSelectedFurniture());
        archiveButton.addActionListener(event -> archiveSelectedFurniture());

        buttons.add(addButton);
        buttons.add(editButton);
        buttons.add(archiveButton);
        panel.add(createStyledScrollPane(furnitureTable), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createOrderPanel() {
        JPanel panel = createCardPanel(new BorderLayout(0, 16));
        JPanel buttons = createActionPanel();
        JButton addButton = createModernButton("Создать заказ");
        JButton nextButton = createSecondaryButton("Следующий статус");
        JButton cancelButton = createSecondaryButton("Отменить заказ");

        configureTable(orderTable);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        addButton.addActionListener(event -> showOrderDialog());
        nextButton.addActionListener(event -> moveSelectedOrder());
        cancelButton.addActionListener(event -> cancelSelectedOrder());

        buttons.add(addButton);
        buttons.add(nextButton);
        buttons.add(cancelButton);
        panel.add(createStyledScrollPane(orderTable), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
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
        table.setIntercellSpacing(new java.awt.Dimension(0, 1));

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

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
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
        JPanel furniturePanel = new JPanel(new GridLayout(0, 1, 0, 6));
        furniturePanel.setBackground(CARD_BACKGROUND);
        List<JCheckBox> checkBoxes = new ArrayList<>();

        for (Furniture furniture : availableFurniture) {
            JCheckBox checkBox = new JCheckBox(furniture.getName() + " - " + furniture.getPrice() + " руб. (" + furniture.getStatus() + ")");
            checkBox.setBackground(CARD_BACKGROUND);
            checkBox.setFont(UI_FONT);
            checkBox.setForeground(TEXT_PRIMARY);
            checkBoxes.add(checkBox);
            furniturePanel.add(checkBox);
        }

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(CARD_BACKGROUND);
        JPanel clientPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        clientPanel.setBackground(CARD_BACKGROUND);
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
        summaryLabel.setText("Мебели в каталоге: " + facade.getAllFurniture().size() + "   •   Заказов: " + facade.getAllOrders().size());
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
