-- ============================================================
-- База данных ООО «СтройМатериалы»
-- СУБД: MySQL 8.0+
-- Нормальная форма: 3NF
-- ============================================================

CREATE DATABASE IF NOT EXISTS stroy_materials
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE stroy_materials;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS pickup_points;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS order_statuses;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS suppliers;
DROP TABLE IF EXISTS manufacturers;
DROP TABLE IF EXISTS units;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- Справочники
-- ============================================================

CREATE TABLE roles (
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE order_statuses (
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE categories (
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE suppliers (
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE manufacturers (
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE units (
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Пункты выдачи
-- ============================================================

CREATE TABLE pickup_points (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    address VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Пользователи
-- ============================================================

CREATE TABLE users (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    role_id   INT          NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    login     VARCHAR(100) NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Товары
-- ============================================================

CREATE TABLE products (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    article         VARCHAR(20)    NOT NULL UNIQUE,
    name            VARCHAR(255)   NOT NULL,
    unit_id         INT            NOT NULL,
    price           DECIMAL(12, 2) NOT NULL,
    supplier_id     INT            NOT NULL,
    manufacturer_id INT            NOT NULL,
    category_id     INT            NOT NULL,
    discount        INT            NOT NULL DEFAULT 0,
    stock_quantity  INT            NOT NULL DEFAULT 0,
    description     TEXT,
    photo           VARCHAR(255),
    CONSTRAINT chk_discount       CHECK (discount >= 0 AND discount <= 100),
    CONSTRAINT chk_stock          CHECK (stock_quantity >= 0),
    CONSTRAINT fk_products_unit   FOREIGN KEY (unit_id)         REFERENCES units(id),
    CONSTRAINT fk_products_sup    FOREIGN KEY (supplier_id)     REFERENCES suppliers(id),
    CONSTRAINT fk_products_manuf  FOREIGN KEY (manufacturer_id) REFERENCES manufacturers(id),
    CONSTRAINT fk_products_cat    FOREIGN KEY (category_id)     REFERENCES categories(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Заказы
-- ============================================================

CREATE TABLE orders (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    order_number    INT  NOT NULL UNIQUE,
    order_date      DATE NOT NULL,
    delivery_date   DATE,
    pickup_point_id INT  NOT NULL,
    user_id         INT  NOT NULL,
    pickup_code     INT  NOT NULL,
    status_id       INT  NOT NULL,
    CONSTRAINT fk_orders_pickup  FOREIGN KEY (pickup_point_id) REFERENCES pickup_points(id),
    CONSTRAINT fk_orders_user    FOREIGN KEY (user_id)         REFERENCES users(id),
    CONSTRAINT fk_orders_status  FOREIGN KEY (status_id)       REFERENCES order_statuses(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Состав заказа
-- ============================================================

CREATE TABLE order_items (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT         NOT NULL,
    article  VARCHAR(20) NOT NULL,
    quantity INT         NOT NULL,
    CONSTRAINT chk_quantity         CHECK (quantity > 0),
    CONSTRAINT fk_items_order       FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_items_product     FOREIGN KEY (article)  REFERENCES products(article)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Индексы
-- ============================================================

CREATE INDEX idx_products_category   ON products(category_id);
CREATE INDEX idx_products_supplier   ON products(supplier_id);
CREATE INDEX idx_orders_user         ON orders(user_id);
CREATE INDEX idx_orders_pickup       ON orders(pickup_point_id);
CREATE INDEX idx_order_items_order   ON order_items(order_id);
CREATE INDEX idx_order_items_article ON order_items(article);
CREATE INDEX idx_users_login         ON users(login);
CREATE INDEX idx_users_role          ON users(role_id);
