package com.stroy.ui;

import com.stroy.database.DatabaseManager;
import com.stroy.model.Manufacturer;
import com.stroy.model.Product;
import com.stroy.model.User;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ProductListForm extends JFrame {

    private final User currentUser;
    private JTextField searchField;
    private JComboBox<String> sortCombo;
    private JComboBox<Object> manufacturerFilterCombo;
    private JTable productTable;
    private ProductTableModel tableModel;
    private JLabel statusLabel;

    // Track open edit form to avoid duplicates
    public ProductEditForm openEditForm = null;

    // All products loaded from DB (unfiltered total for status bar)
    private List<Product> allProducts = new ArrayList<>();

    private static final String[] SORT_OPTIONS = {
        "— Сортировка —",
        "Количество ↑", "Количество ↓",
        "Цена ↑", "Цена ↓",
        "Скидка ↑", "Скидка ↓"
    };

    private static final String[] COLUMN_NAMES = {
        "ID", "Наименование", "Категория", "Производитель", "Цена (руб.)", "Кол-во", "Скидка (%)", "Единица изм."
    };

    public ProductListForm(User user) {
        this.currentUser = user;

        String roleDisplay = user.isAdmin() ? "Администратор" : "Менеджер";
        setTitle("Список товаров — " + user.getUsername() + " (" + roleDisplay + ")");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                confirmLogout();
            }
        });

        initComponents();
        refreshTable();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // NORTH: toolbar
        add(buildToolbar(), BorderLayout.NORTH);

        // CENTER: product table
        tableModel = new ProductTableModel();
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        productTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        productTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        productTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        productTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        productTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        productTable.getColumnModel().getColumn(5).setPreferredWidth(60);
        productTable.getColumnModel().getColumn(6).setPreferredWidth(70);
        productTable.getColumnModel().getColumn(7).setPreferredWidth(80);

        // Right-align price and discount columns
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        productTable.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        productTable.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);

        // Double-click to edit (admin only)
        productTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && currentUser.isAdmin()) {
                    openEditSelected();
                }
            }
        });

        add(new JScrollPane(productTable), BorderLayout.CENTER);

        // SOUTH: status bar + action buttons
        add(buildSouthPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        toolbar.setBorder(BorderFactory.createEtchedBorder());

        // Logout button
        JButton logoutBtn = new JButton("Выйти");
        logoutBtn.addActionListener(e -> confirmLogout());
        toolbar.add(logoutBtn);

        toolbar.add(new JSeparator(SwingConstants.VERTICAL));

        // Search
        toolbar.add(new JLabel("Поиск:"));
        searchField = new JTextField(15);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshTable(); }
            public void removeUpdate(DocumentEvent e) { refreshTable(); }
            public void changedUpdate(DocumentEvent e) { refreshTable(); }
        });
        toolbar.add(searchField);

        // Sort combo
        sortCombo = new JComboBox<>(SORT_OPTIONS);
        sortCombo.addActionListener(e -> refreshTable());
        toolbar.add(sortCombo);

        // Manufacturer filter combo
        manufacturerFilterCombo = new JComboBox<>();
        manufacturerFilterCombo.addItem("Все производители");
        for (Manufacturer m : DatabaseManager.getManufacturers()) {
            manufacturerFilterCombo.addItem(m);
        }
        manufacturerFilterCombo.addActionListener(e -> refreshTable());
        toolbar.add(manufacturerFilterCombo);

        // Add product button (admin only)
        if (currentUser.isAdmin()) {
            JButton addBtn = new JButton("Добавить товар");
            addBtn.addActionListener(e -> openAddForm());
            toolbar.add(addBtn);
        }

        return toolbar;
    }

    private JPanel buildSouthPanel() {
        JPanel south = new JPanel(new BorderLayout());
        south.setBorder(BorderFactory.createEtchedBorder());

        // Status bar
        statusLabel = new JLabel("  Загрузка...");
        south.add(statusLabel, BorderLayout.WEST);

        // Action buttons panel (edit and delete are admin-only per assignment)
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 3));

        if (currentUser.isAdmin()) {
            JButton editBtn = new JButton("Редактировать");
            editBtn.addActionListener(e -> openEditSelected());
            buttonsPanel.add(editBtn);

            JButton deleteBtn = new JButton("Удалить");
            deleteBtn.addActionListener(e -> deleteSelected());
            buttonsPanel.add(deleteBtn);
        }

        south.add(buttonsPanel, BorderLayout.EAST);
        return south;
    }

    private void refreshTable() {
        String search = searchField != null ? searchField.getText() : "";
        int sortIndex = sortCombo != null ? sortCombo.getSelectedIndex() : 0;

        String sortBy = null;
        boolean ascending = true;
        switch (sortIndex) {
            case 1: sortBy = "quantity"; ascending = true; break;
            case 2: sortBy = "quantity"; ascending = false; break;
            case 3: sortBy = "price"; ascending = true; break;
            case 4: sortBy = "price"; ascending = false; break;
            case 5: sortBy = "discount"; ascending = true; break;
            case 6: sortBy = "discount"; ascending = false; break;
        }

        int manufacturerFilter = 0;
        if (manufacturerFilterCombo != null && manufacturerFilterCombo.getSelectedIndex() > 0) {
            Object selected = manufacturerFilterCombo.getSelectedItem();
            if (selected instanceof Manufacturer) {
                manufacturerFilter = ((Manufacturer) selected).getId();
            }
        }

        List<Product> filtered = DatabaseManager.getProducts(search, sortBy, ascending, manufacturerFilter);
        // Total without filter/search for status bar
        List<Product> total = DatabaseManager.getProducts("", null, true, 0);

        tableModel.setProducts(filtered);
        if (statusLabel != null) {
            statusLabel.setText("  Показано: " + filtered.size() + " из " + total.size() + " товаров");
        }
    }

    public void refreshProducts() {
        refreshTable();
    }

    private void openAddForm() {
        if (openEditForm != null) {
            openEditForm.toFront();
            JOptionPane.showMessageDialog(this,
                    "Окно редактирования уже открыто.",
                    "Предупреждение", JOptionPane.WARNING_MESSAGE);
            return;
        }
        openEditForm = new ProductEditForm(this, currentUser, null);
    }

    private void openEditSelected() {
        int row = productTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Выберите товар для редактирования.",
                    "Предупреждение", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (openEditForm != null) {
            openEditForm.toFront();
            JOptionPane.showMessageDialog(this,
                    "Окно редактирования уже открыто.",
                    "Предупреждение", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Product product = tableModel.getProductAt(row);
        openEditForm = new ProductEditForm(this, currentUser, product);
    }

    private void deleteSelected() {
        int row = productTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Выберите товар для удаления.",
                    "Предупреждение", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Product product = tableModel.getProductAt(row);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Вы уверены, что хотите удалить товар \"" + product.getName() + "\"?",
                "Подтверждение", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            DatabaseManager.deleteProduct(product.getId());
            refreshTable();
            JOptionPane.showMessageDialog(this,
                    "Товар успешно удалён.",
                    "Информация", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка удаления: " + ex.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void confirmLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Вы уверены, что хотите выйти из системы?",
                "Подтверждение", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(LoginForm::new);
        }
    }

    // Inner table model
    private static class ProductTableModel extends AbstractTableModel {
        private List<Product> products = new ArrayList<>();

        public void setProducts(List<Product> products) {
            this.products = new ArrayList<>(products);
            fireTableDataChanged();
        }

        public Product getProductAt(int row) {
            return products.get(row);
        }

        @Override
        public int getRowCount() { return products.size(); }

        @Override
        public int getColumnCount() { return COLUMN_NAMES.length; }

        @Override
        public String getColumnName(int col) { return COLUMN_NAMES[col]; }

        @Override
        public boolean isCellEditable(int row, int col) { return false; }

        @Override
        public Object getValueAt(int row, int col) {
            Product p = products.get(row);
            switch (col) {
                case 0: return p.getId();
                case 1: return p.getName();
                case 2: return p.getCategoryName();
                case 3: return p.getManufacturerName();
                case 4: return String.format("%.2f", p.getPrice());
                case 5: return p.getQuantity();
                case 6: return String.format("%.1f", p.getDiscount());
                case 7: return p.getUnit() != null ? p.getUnit() : "";
                default: return "";
            }
        }
    }
}
