package com.stroy.model;

public class Product {
    private int id;
    private String name;
    private int categoryId;
    private String categoryName;
    private String description;
    private int manufacturerId;
    private String manufacturerName;
    private String supplier;
    private double price;
    private String unit;
    private int quantity;
    private double discount;
    private String imagePath;

    public Product(int id, String name, int categoryId, String categoryName,
                   String description, int manufacturerId, String manufacturerName,
                   String supplier, double price, String unit, int quantity,
                   double discount, String imagePath) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
        this.manufacturerId = manufacturerId;
        this.manufacturerName = manufacturerName;
        this.supplier = supplier;
        this.price = price;
        this.unit = unit;
        this.quantity = quantity;
        this.discount = discount;
        this.imagePath = imagePath;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getManufacturerId() { return manufacturerId; }
    public void setManufacturerId(int manufacturerId) { this.manufacturerId = manufacturerId; }

    public String getManufacturerName() { return manufacturerName; }
    public void setManufacturerName(String manufacturerName) { this.manufacturerName = manufacturerName; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}
