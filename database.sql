-- ============================================================
-- База данных ООО «СтройМатериалы»
-- СУБД: PostgreSQL
-- Нормальная форма: 3NF
-- ============================================================

-- Пересоздание схемы
DROP TABLE IF EXISTS order_items    CASCADE;
DROP TABLE IF EXISTS orders         CASCADE;
DROP TABLE IF EXISTS products       CASCADE;
DROP TABLE IF EXISTS pickup_points  CASCADE;
DROP TABLE IF EXISTS users          CASCADE;
DROP TABLE IF EXISTS roles          CASCADE;
DROP TABLE IF EXISTS order_statuses CASCADE;
DROP TABLE IF EXISTS categories     CASCADE;
DROP TABLE IF EXISTS suppliers      CASCADE;
DROP TABLE IF EXISTS manufacturers  CASCADE;
DROP TABLE IF EXISTS units          CASCADE;

-- ============================================================
-- Справочники (1NF → 3NF: выносим повторяющиеся строки)
-- ============================================================

CREATE TABLE roles (
    id   SERIAL      PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE order_statuses (
    id   SERIAL      PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE categories (
    id   SERIAL       PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE suppliers (
    id   SERIAL       PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE manufacturers (
    id   SERIAL       PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE units (
    id   SERIAL      PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

-- ============================================================
-- Пункты выдачи
-- ============================================================

CREATE TABLE pickup_points (
    id      SERIAL       PRIMARY KEY,
    address VARCHAR(255) NOT NULL
);

-- ============================================================
-- Пользователи
-- ============================================================

CREATE TABLE users (
    id        SERIAL       PRIMARY KEY,
    role_id   INT          NOT NULL REFERENCES roles(id),
    full_name VARCHAR(150) NOT NULL,
    login     VARCHAR(100) NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL
);

-- ============================================================
-- Товары
-- ============================================================

CREATE TABLE products (
    id               SERIAL         PRIMARY KEY,
    article          VARCHAR(20)    NOT NULL UNIQUE,
    name             VARCHAR(255)   NOT NULL,
    unit_id          INT            NOT NULL REFERENCES units(id),
    price            NUMERIC(12, 2) NOT NULL,
    supplier_id      INT            NOT NULL REFERENCES suppliers(id),
    manufacturer_id  INT            NOT NULL REFERENCES manufacturers(id),
    category_id      INT            NOT NULL REFERENCES categories(id),
    discount         INT            NOT NULL DEFAULT 0 CHECK (discount >= 0 AND discount <= 100),
    stock_quantity   INT            NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    description      TEXT,
    photo            VARCHAR(255)
);

-- ============================================================
-- Заказы
-- ============================================================

CREATE TABLE orders (
    id               SERIAL  PRIMARY KEY,
    order_number     INT     NOT NULL UNIQUE,
    order_date       DATE    NOT NULL,
    delivery_date    DATE,
    pickup_point_id  INT     NOT NULL REFERENCES pickup_points(id),
    user_id          INT     NOT NULL REFERENCES users(id),
    pickup_code      INT     NOT NULL,
    status_id        INT     NOT NULL REFERENCES order_statuses(id)
);

-- ============================================================
-- Состав заказа (нормализация: многие-ко-многим orders ↔ products)
-- ============================================================

CREATE TABLE order_items (
    id         SERIAL      PRIMARY KEY,
    order_id   INT         NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    article    VARCHAR(20) NOT NULL REFERENCES products(article),
    quantity   INT         NOT NULL CHECK (quantity > 0)
);

-- ============================================================
-- Индексы для ускорения выборок
-- ============================================================

CREATE INDEX idx_products_category    ON products(category_id);
CREATE INDEX idx_products_supplier    ON products(supplier_id);
CREATE INDEX idx_products_article     ON products(article);
CREATE INDEX idx_orders_user          ON orders(user_id);
CREATE INDEX idx_orders_pickup_point  ON orders(pickup_point_id);
CREATE INDEX idx_order_items_order    ON order_items(order_id);
CREATE INDEX idx_order_items_article  ON order_items(article);
CREATE INDEX idx_users_login          ON users(login);
CREATE INDEX idx_users_role           ON users(role_id);
