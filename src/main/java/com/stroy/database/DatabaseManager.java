package com.stroy.database;

import com.stroy.model.Category;
import com.stroy.model.Manufacturer;
import com.stroy.model.Product;
import com.stroy.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:stroy.db";

    public static void initialize() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS categories (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS manufacturers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT NOT NULL UNIQUE, " +
                    "password TEXT NOT NULL, " +
                    "role TEXT NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS products (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "category_id INTEGER REFERENCES categories(id), " +
                    "description TEXT, " +
                    "manufacturer_id INTEGER REFERENCES manufacturers(id), " +
                    "supplier TEXT, " +
                    "price REAL NOT NULL DEFAULT 0, " +
                    "unit TEXT, " +
                    "quantity INTEGER NOT NULL DEFAULT 0, " +
                    "discount REAL DEFAULT 0, " +
                    "image_path TEXT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS orders (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "created_at TEXT NOT NULL, " +
                    "user_id INTEGER REFERENCES users(id))");

            stmt.execute("CREATE TABLE IF NOT EXISTS order_items (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "order_id INTEGER REFERENCES orders(id), " +
                    "product_id INTEGER REFERENCES products(id), " +
                    "quantity INTEGER NOT NULL, " +
                    "price REAL NOT NULL)");

            insertDefaultData(conn);

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка инициализации базы данных: " + e.getMessage(), e);
        }
    }

    private static void insertDefaultData(Connection conn) throws SQLException {
        // Insert categories if empty
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM categories")) {
            if (rs.getInt(1) == 0) {
                String[] categories = {
                    "Цемент", "Кирпич", "Металлопрокат", "Трубы",
                    "Утеплители", "Краска", "Инструменты", "Плитка"
                };
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO categories (name) VALUES (?)")) {
                    for (String cat : categories) {
                        ps.setString(1, cat);
                        ps.executeUpdate();
                    }
                }
            }
        }

        // Insert manufacturers if empty
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM manufacturers")) {
            if (rs.getInt(1) == 0) {
                String[] manufacturers = {
                    "ЛСР Групп", "КНАУФ", "Технониколь", "Bosch",
                    "Rehau", "Caparol", "СтройМикс", "ЦеSiт"
                };
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO manufacturers (name) VALUES (?)")) {
                    for (String mfr : manufacturers) {
                        ps.setString(1, mfr);
                        ps.executeUpdate();
                    }
                }
            }
        }

        // Insert users if empty
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            if (rs.getInt(1) == 0) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO users (username, password, role) VALUES (?, ?, ?)")) {
                    ps.setString(1, "admin");
                    ps.setString(2, "admin123");
                    ps.setString(3, "admin");
                    ps.executeUpdate();

                    ps.setString(1, "manager");
                    ps.setString(2, "manager123");
                    ps.setString(3, "manager");
                    ps.executeUpdate();
                }
            }
        }

        // Insert sample products if empty
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM products")) {
            if (rs.getInt(1) == 0) {
                // Get category and manufacturer IDs
                int cementId = getCategoryIdByName(conn, "Цемент");
                int brickId = getCategoryIdByName(conn, "Кирпич");
                int paintId = getCategoryIdByName(conn, "Краска");
                int toolsId = getCategoryIdByName(conn, "Инструменты");
                int insulId = getCategoryIdByName(conn, "Утеплители");

                int lsrId = getManufacturerIdByName(conn, "ЛСР Групп");
                int knaufId = getManufacturerIdByName(conn, "КНАУФ");
                int caparolId = getManufacturerIdByName(conn, "Caparol");
                int boschId = getManufacturerIdByName(conn, "Bosch");
                int tehnoId = getManufacturerIdByName(conn, "Технониколь");

                String sql = "INSERT INTO products (name, category_id, description, manufacturer_id, supplier, price, unit, quantity, discount, image_path) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    // Product 1
                    ps.setString(1, "Цемент М500 50кг");
                    ps.setInt(2, cementId);
                    ps.setString(3, "Портландцемент М500, мешок 50 кг. Применяется для приготовления бетонных и растворных смесей.");
                    ps.setInt(4, lsrId);
                    ps.setString(5, "ООО СтройСнаб");
                    ps.setDouble(6, 385.00);
                    ps.setString(7, "мешок");
                    ps.setInt(8, 250);
                    ps.setDouble(9, 5.0);
                    ps.setString(10, null);
                    ps.executeUpdate();

                    // Product 2
                    ps.setString(1, "Кирпич рядовой М150");
                    ps.setInt(2, brickId);
                    ps.setString(3, "Кирпич керамический рядовой полнотелый М150. Размер 250x120x65 мм.");
                    ps.setInt(4, lsrId);
                    ps.setString(5, "ООО КирпичТорг");
                    ps.setDouble(6, 18.50);
                    ps.setString(7, "шт");
                    ps.setInt(8, 5000);
                    ps.setDouble(9, 0.0);
                    ps.setString(10, null);
                    ps.executeUpdate();

                    // Product 3
                    ps.setString(1, "Шпаклёвка финишная KNAUF");
                    ps.setInt(2, paintId);
                    ps.setString(3, "Финишная шпаклёвка для внутренних работ. Мешок 25 кг.");
                    ps.setInt(4, knaufId);
                    ps.setString(5, "ООО ДСТ-Трейд");
                    ps.setDouble(6, 520.00);
                    ps.setString(7, "мешок");
                    ps.setInt(8, 120);
                    ps.setDouble(9, 10.0);
                    ps.setString(10, null);
                    ps.executeUpdate();

                    // Product 4
                    ps.setString(1, "Перфоратор Bosch GBH 2-26");
                    ps.setInt(2, toolsId);
                    ps.setString(3, "Перфоратор Bosch GBH 2-26 DRE Professional, 800 Вт, SDS-plus.");
                    ps.setInt(4, boschId);
                    ps.setString(5, "ООО ИнструментПлюс");
                    ps.setDouble(6, 12500.00);
                    ps.setString(7, "шт");
                    ps.setInt(8, 15);
                    ps.setDouble(9, 7.5);
                    ps.setString(10, null);
                    ps.executeUpdate();

                    // Product 5
                    ps.setString(1, "Минвата Технониколь ROCKLIGHT");
                    ps.setInt(2, insulId);
                    ps.setString(3, "Теплоизоляционные плиты из каменной ваты. Упаковка 0.432 м3.");
                    ps.setInt(4, tehnoId);
                    ps.setString(5, "ООО УтеплительТорг");
                    ps.setDouble(6, 2850.00);
                    ps.setString(7, "уп");
                    ps.setInt(8, 80);
                    ps.setDouble(9, 3.0);
                    ps.setString(10, null);
                    ps.executeUpdate();

                    // Product 6
                    ps.setString(1, "Краска фасадная Caparol Amphibolin");
                    ps.setInt(2, paintId);
                    ps.setString(3, "Универсальная фасадная краска, 10 л. Устойчива к атмосферным воздействиям.");
                    ps.setInt(4, caparolId);
                    ps.setString(5, "ООО КрасокМаркет");
                    ps.setDouble(6, 4200.00);
                    ps.setString(7, "ведро");
                    ps.setInt(8, 45);
                    ps.setDouble(9, 0.0);
                    ps.setString(10, null);
                    ps.executeUpdate();
                }
            }
        }
    }

    private static int getCategoryIdByName(Connection conn, String name) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id FROM categories WHERE name = ?")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 1;
            }
        }
    }

    private static int getManufacturerIdByName(Connection conn, String name) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id FROM manufacturers WHERE name = ?")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 1;
            }
        }
    }

    public static User authenticate(String username, String password) {
        String sql = "SELECT id, username, role FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("username"), rs.getString("role"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка аутентификации: " + e.getMessage(), e);
        }
        return null;
    }

    public static List<Product> getProducts(String search, String sortBy, boolean ascending, int manufacturerIdFilter) {
        List<Product> products = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT p.id, p.name, p.category_id, COALESCE(c.name,'') AS category_name, " +
                "p.description, p.manufacturer_id, COALESCE(m.name,'') AS manufacturer_name, " +
                "p.supplier, p.price, p.unit, p.quantity, p.discount, p.image_path " +
                "FROM products p " +
                "LEFT JOIN categories c ON p.category_id = c.id " +
                "LEFT JOIN manufacturers m ON p.manufacturer_id = m.id " +
                "WHERE 1=1");

        List<Object> params = new ArrayList<>();

        if (search != null && !search.trim().isEmpty()) {
            String pattern = "%" + search.trim().toLowerCase() + "%";
            sql.append(" AND (LOWER(p.name) LIKE ? OR LOWER(COALESCE(p.description,'')) LIKE ? " +
                    "OR LOWER(COALESCE(p.supplier,'')) LIKE ? OR LOWER(COALESCE(c.name,'')) LIKE ? " +
                    "OR LOWER(COALESCE(m.name,'')) LIKE ? OR LOWER(COALESCE(p.unit,'')) LIKE ?)");
            for (int i = 0; i < 6; i++) params.add(pattern);
        }

        if (manufacturerIdFilter > 0) {
            sql.append(" AND p.manufacturer_id = ?");
            params.add(manufacturerIdFilter);
        }

        if (sortBy != null) {
            switch (sortBy) {
                case "quantity":
                    sql.append(" ORDER BY p.quantity ").append(ascending ? "ASC" : "DESC");
                    break;
                case "price":
                    sql.append(" ORDER BY p.price ").append(ascending ? "ASC" : "DESC");
                    break;
                case "discount":
                    sql.append(" ORDER BY p.discount ").append(ascending ? "ASC" : "DESC");
                    break;
                default:
                    sql.append(" ORDER BY p.id ASC");
                    break;
            }
        } else {
            sql.append(" ORDER BY p.id ASC");
        }

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("category_id"),
                            rs.getString("category_name"),
                            rs.getString("description"),
                            rs.getInt("manufacturer_id"),
                            rs.getString("manufacturer_name"),
                            rs.getString("supplier"),
                            rs.getDouble("price"),
                            rs.getString("unit"),
                            rs.getInt("quantity"),
                            rs.getDouble("discount"),
                            rs.getString("image_path")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка загрузки товаров: " + e.getMessage(), e);
        }
        return products;
    }

    public static List<Category> getCategories() {
        List<Category> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name FROM categories ORDER BY name")) {
            while (rs.next()) {
                list.add(new Category(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка загрузки категорий: " + e.getMessage(), e);
        }
        return list;
    }

    public static List<Manufacturer> getManufacturers() {
        List<Manufacturer> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name FROM manufacturers ORDER BY name")) {
            while (rs.next()) {
                list.add(new Manufacturer(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка загрузки производителей: " + e.getMessage(), e);
        }
        return list;
    }

    public static void addProduct(Product product) throws SQLException {
        String sql = "INSERT INTO products (name, category_id, description, manufacturer_id, supplier, price, unit, quantity, discount, image_path) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setInt(2, product.getCategoryId());
            ps.setString(3, product.getDescription());
            ps.setInt(4, product.getManufacturerId());
            ps.setString(5, product.getSupplier());
            ps.setDouble(6, product.getPrice());
            ps.setString(7, product.getUnit());
            ps.setInt(8, product.getQuantity());
            ps.setDouble(9, product.getDiscount());
            ps.setString(10, product.getImagePath());
            ps.executeUpdate();
        }
    }

    public static void updateProduct(Product product) throws SQLException {
        String sql = "UPDATE products SET name=?, category_id=?, description=?, manufacturer_id=?, supplier=?, price=?, unit=?, quantity=?, discount=?, image_path=? WHERE id=?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setInt(2, product.getCategoryId());
            ps.setString(3, product.getDescription());
            ps.setInt(4, product.getManufacturerId());
            ps.setString(5, product.getSupplier());
            ps.setDouble(6, product.getPrice());
            ps.setString(7, product.getUnit());
            ps.setInt(8, product.getQuantity());
            ps.setDouble(9, product.getDiscount());
            ps.setString(10, product.getImagePath());
            ps.setInt(11, product.getId());
            ps.executeUpdate();
        }
    }

    public static void deleteProduct(int productId) throws Exception {
        if (isProductInOrder(productId)) {
            throw new Exception("Невозможно удалить товар: он используется в заказах.");
        }
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement("DELETE FROM products WHERE id = ?")) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        }
    }

    public static boolean isProductInOrder(int productId) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT COUNT(*) FROM order_items WHERE product_id = ?")) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public static int getNextProductId() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COALESCE(MAX(id), 0) + 1 FROM products")) {
            return rs.next() ? rs.getInt(1) : 1;
        } catch (SQLException e) {
            return 1;
        }
    }
}
