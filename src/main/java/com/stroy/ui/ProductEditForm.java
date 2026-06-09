package com.stroy.ui;

import com.stroy.database.DatabaseManager;
import com.stroy.model.Category;
import com.stroy.model.Manufacturer;
import com.stroy.model.Product;
import com.stroy.model.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class ProductEditForm extends JFrame {

    private final ProductListForm parent;
    private final User currentUser;
    private final Product editProduct; // null = add mode
    private final boolean isAddMode;

    // Form fields
    private JTextField idField;
    private JLabel imageLabel;
    private JTextField nameField;
    private JComboBox<Category> categoryCombo;
    private JTextArea descriptionArea;
    private JComboBox<Manufacturer> manufacturerCombo;
    private JTextField supplierField;
    private JTextField priceField;
    private JTextField unitField;
    private JTextField quantityField;
    private JTextField discountField;

    // Currently selected image path (relative or absolute)
    private String currentImagePath;
    // Image path before editing (to allow rollback on cancel)
    private String originalImagePath;

    // Track if any field was changed for cancel confirmation
    private boolean fieldChanged = false;

    public ProductEditForm(ProductListForm parent, User currentUser, Product product) {
        this.parent = parent;
        this.currentUser = currentUser;
        this.editProduct = product;
        this.isAddMode = (product == null);

        if (isAddMode) {
            setTitle("Добавление товара");
            currentImagePath = null;
            originalImagePath = null;
        } else {
            setTitle("Редактирование товара — " + product.getName());
            currentImagePath = product.getImagePath();
            originalImagePath = product.getImagePath();
        }

        setSize(600, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleClose();
            }
        });

        initComponents();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Fields panel
        mainPanel.add(buildFieldsPanel(), BorderLayout.CENTER);

        // Buttons panel
        mainPanel.add(buildButtonsPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel buildFieldsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // ID field (only in edit mode)
        if (!isAddMode) {
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
            panel.add(new JLabel("ID:"), gbc);
            idField = new JTextField(5);
            idField.setEditable(false);
            idField.setText(String.valueOf(editProduct.getId()));
            idField.setBackground(UIManager.getColor("TextField.disabledBackground"));
            gbc.gridx = 1; gbc.weightx = 1;
            panel.add(idField, gbc);
            row++;
        }

        // Image section
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.gridwidth = 1;
        panel.add(new JLabel("Изображение:"), gbc);

        JPanel imagePanel = new JPanel(new BorderLayout(5, 0));
        imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(300, 200));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        updateImagePreview();

        JButton chooseImageBtn = new JButton("Выбрать изображение");
        chooseImageBtn.addActionListener(e -> chooseImage());

        imagePanel.add(imageLabel, BorderLayout.CENTER);
        imagePanel.add(chooseImageBtn, BorderLayout.SOUTH);

        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1;
        panel.add(imagePanel, gbc);
        row++;

        // Name
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel("Наименование:"), gbc);
        nameField = new JTextField(25);
        if (!isAddMode) nameField.setText(editProduct.getName());
        nameField.getDocument().addDocumentListener(changeListener());
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(nameField, gbc);
        row++;

        // Category
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel("Категория:"), gbc);
        categoryCombo = new JComboBox<>();
        for (Category c : DatabaseManager.getCategories()) {
            categoryCombo.addItem(c);
        }
        if (!isAddMode) {
            selectComboById(categoryCombo, editProduct.getCategoryId());
        }
        categoryCombo.addActionListener(e -> fieldChanged = true);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(categoryCombo, gbc);
        row++;

        // Description
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Описание:"), gbc);
        descriptionArea = new JTextArea(3, 25);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        if (!isAddMode && editProduct.getDescription() != null) {
            descriptionArea.setText(editProduct.getDescription());
        }
        descriptionArea.getDocument().addDocumentListener(changeListener());
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setPreferredSize(new Dimension(300, 70));
        gbc.gridx = 1; gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(descScrollPane, gbc);
        row++;

        // Manufacturer
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel("Производитель:"), gbc);
        manufacturerCombo = new JComboBox<>();
        for (Manufacturer m : DatabaseManager.getManufacturers()) {
            manufacturerCombo.addItem(m);
        }
        if (!isAddMode) {
            selectManufacturerById(manufacturerCombo, editProduct.getManufacturerId());
        }
        manufacturerCombo.addActionListener(e -> fieldChanged = true);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(manufacturerCombo, gbc);
        row++;

        // Supplier
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel("Поставщик:"), gbc);
        supplierField = new JTextField(25);
        if (!isAddMode && editProduct.getSupplier() != null) supplierField.setText(editProduct.getSupplier());
        supplierField.getDocument().addDocumentListener(changeListener());
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(supplierField, gbc);
        row++;

        // Price
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel("Цена (руб.):"), gbc);
        priceField = new JTextField(10);
        if (!isAddMode) priceField.setText(String.format("%.2f", editProduct.getPrice()));
        priceField.getDocument().addDocumentListener(changeListener());
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(priceField, gbc);
        row++;

        // Unit
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel("Единица измерения:"), gbc);
        unitField = new JTextField(10);
        if (!isAddMode && editProduct.getUnit() != null) unitField.setText(editProduct.getUnit());
        unitField.getDocument().addDocumentListener(changeListener());
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(unitField, gbc);
        row++;

        // Quantity
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel("Количество на складе:"), gbc);
        quantityField = new JTextField(10);
        if (!isAddMode) quantityField.setText(String.valueOf(editProduct.getQuantity()));
        quantityField.getDocument().addDocumentListener(changeListener());
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(quantityField, gbc);
        row++;

        // Discount
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel("Действующая скидка (%):"), gbc);
        discountField = new JTextField(10);
        if (!isAddMode) discountField.setText(String.format("%.1f", editProduct.getDiscount()));
        discountField.getDocument().addDocumentListener(changeListener());
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(discountField, gbc);

        // Wrap in a scroll pane in case content doesn't fit
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton saveBtn = new JButton("Сохранить");
        saveBtn.addActionListener(e -> saveProduct());
        panel.add(saveBtn);

        JButton cancelBtn = new JButton("Отмена");
        cancelBtn.addActionListener(e -> handleClose());
        panel.add(cancelBtn);

        return panel;
    }

    private void chooseImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Выбрать изображение");
        chooser.setFileFilter(new FileNameExtensionFilter(
                "Изображения (jpg, jpeg, png, gif)", "jpg", "jpeg", "png", "gif"));
        chooser.setAcceptAllFileFilterUsed(false);

        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File selectedFile = chooser.getSelectedFile();
        if (!selectedFile.exists()) {
            JOptionPane.showMessageDialog(this,
                    "Выбранный файл не найден.",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Delete old custom image if it exists and is in images/ dir
            if (currentImagePath != null && currentImagePath.startsWith("images/")) {
                File oldFile = new File(currentImagePath);
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }

            // Copy image to images/ directory with a name based on product name + timestamp
            String sanitizedName = sanitizeFileName(nameField.getText().trim());
            if (sanitizedName.isEmpty()) sanitizedName = "product";
            String ext = getFileExtension(selectedFile.getName());
            String destFileName = sanitizedName + "_" + System.currentTimeMillis() + "." + ext;
            File destDir = new File("images");
            if (!destDir.exists()) destDir.mkdirs();
            File destFile = new File(destDir, destFileName);

            // Scale image to max 300x200 and save
            BufferedImage original = ImageIO.read(selectedFile);
            if (original == null) {
                JOptionPane.showMessageDialog(this,
                        "Не удалось прочитать изображение.",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            BufferedImage scaled = scaleImage(original, 300, 200);
            String outputFormat = ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg") ? "jpg" : "png";
            ImageIO.write(scaled, outputFormat, destFile);

            currentImagePath = "images/" + destFileName;
            fieldChanged = true;
            updateImagePreview();

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка при копировании изображения: " + ex.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateImagePreview() {
        try {
            File imgFile = null;
            if (currentImagePath != null && !currentImagePath.isEmpty()) {
                imgFile = new File(currentImagePath);
            }
            if (imgFile == null || !imgFile.exists()) {
                imgFile = new File("picture.png");
            }
            if (imgFile.exists()) {
                BufferedImage img = ImageIO.read(imgFile);
                if (img != null) {
                    BufferedImage scaled = scaleImage(img, 300, 200);
                    imageLabel.setIcon(new ImageIcon(scaled));
                    imageLabel.setText("");
                    return;
                }
            }
        } catch (IOException ignored) {}
        imageLabel.setIcon(null);
        imageLabel.setText("Нет изображения");
    }

    private BufferedImage scaleImage(BufferedImage src, int maxWidth, int maxHeight) {
        int w = src.getWidth();
        int h = src.getHeight();
        double scale = Math.min((double) maxWidth / w, (double) maxHeight / h);
        if (scale >= 1.0) {
            // No need to scale up; center on a maxWidth x maxHeight canvas
            BufferedImage dest = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = dest.createGraphics();
            g.setColor(new Color(180, 180, 180));
            g.fillRect(0, 0, maxWidth, maxHeight);
            int x = (maxWidth - w) / 2;
            int y = (maxHeight - h) / 2;
            g.drawImage(src, x, y, null);
            g.dispose();
            return dest;
        }
        int newW = (int) (w * scale);
        int newH = (int) (h * scale);
        BufferedImage dest = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = dest.createGraphics();
        g.setColor(new Color(180, 180, 180));
        g.fillRect(0, 0, maxWidth, maxHeight);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        int x = (maxWidth - newW) / 2;
        int y = (maxHeight - newH) / 2;
        g.drawImage(src, x, y, newW, newH, null);
        g.dispose();
        return dest;
    }

    private void saveProduct() {
        List<String> errors = new ArrayList<>();

        // Validate name
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            errors.add("Наименование товара не может быть пустым.");
        }

        // Validate category
        Category selectedCategory = (Category) categoryCombo.getSelectedItem();
        if (selectedCategory == null) {
            errors.add("Необходимо выбрать категорию.");
        }

        // Validate manufacturer
        Manufacturer selectedManufacturer = (Manufacturer) manufacturerCombo.getSelectedItem();
        if (selectedManufacturer == null) {
            errors.add("Необходимо выбрать производителя.");
        }

        // Validate price
        double price = 0;
        String priceText = priceField.getText().trim().replace(",", ".");
        try {
            price = Double.parseDouble(priceText);
            if (price < 0) errors.add("Цена не может быть отрицательной.");
        } catch (NumberFormatException e) {
            errors.add("Цена должна быть числом (например: 150.00).");
        }

        // Validate quantity
        int quantity = 0;
        String quantityText = quantityField.getText().trim();
        try {
            quantity = Integer.parseInt(quantityText);
            if (quantity < 0) errors.add("Количество не может быть отрицательным.");
        } catch (NumberFormatException e) {
            errors.add("Количество должно быть целым числом.");
        }

        // Validate discount
        double discount = 0;
        String discountText = discountField.getText().trim().replace(",", ".");
        if (!discountText.isEmpty()) {
            try {
                discount = Double.parseDouble(discountText);
                if (discount < 0 || discount > 100) {
                    errors.add("Скидка должна быть в диапазоне от 0 до 100.");
                }
            } catch (NumberFormatException e) {
                errors.add("Скидка должна быть числом (например: 10.0).");
            }
        }

        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder("Обнаружены следующие ошибки:\n");
            for (String err : errors) {
                sb.append("• ").append(err).append("\n");
            }
            JOptionPane.showMessageDialog(this,
                    sb.toString(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Build product object
        int id = isAddMode ? 0 : editProduct.getId();
        String description = descriptionArea.getText().trim();
        String supplier = supplierField.getText().trim();
        String unit = unitField.getText().trim();

        Product product = new Product(
                id, name,
                selectedCategory.getId(), selectedCategory.getName(),
                description.isEmpty() ? null : description,
                selectedManufacturer.getId(), selectedManufacturer.getName(),
                supplier.isEmpty() ? null : supplier,
                price, unit.isEmpty() ? null : unit,
                quantity, discount,
                currentImagePath
        );

        try {
            if (isAddMode) {
                DatabaseManager.addProduct(product);
            } else {
                DatabaseManager.updateProduct(product);
            }
            parent.refreshProducts();
            parent.openEditForm = null;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка сохранения: " + ex.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleClose() {
        if (fieldChanged) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Есть несохранённые изменения. Вы уверены, что хотите закрыть окно?",
                    "Подтверждение", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;
        }
        parent.openEditForm = null;
        dispose();
    }

    // Helper: select combo item by category id
    private void selectComboById(JComboBox<Category> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Category c = combo.getItemAt(i);
            if (c.getId() == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    // Helper: select combo item by manufacturer id
    private void selectManufacturerById(JComboBox<Manufacturer> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Manufacturer m = combo.getItemAt(i);
            if (m.getId() == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private String sanitizeFileName(String name) {
        return name.replaceAll("[^а-яА-ЯёЁa-zA-Z0-9_\\-]", "_");
    }

    private String getFileExtension(String fileName) {
        int dotIdx = fileName.lastIndexOf('.');
        if (dotIdx >= 0 && dotIdx < fileName.length() - 1) {
            return fileName.substring(dotIdx + 1).toLowerCase();
        }
        return "png";
    }

    // Returns a DocumentListener that marks fieldChanged = true on any change
    private javax.swing.event.DocumentListener changeListener() {
        return new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { fieldChanged = true; }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { fieldChanged = true; }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { fieldChanged = true; }
        };
    }
}
