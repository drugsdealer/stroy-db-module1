-- ============================================================
-- Импорт данных ООО «СтройМатериалы»
-- Источники: Tovar.xlsx, user_import.xlsx,
--            Заказ_import.xlsx, Пункты выдачи_import.xlsx
-- ============================================================

-- --------------------------------------------------------
-- Справочник ролей
-- --------------------------------------------------------
INSERT INTO roles (name) VALUES
    ('Администратор'),
    ('Менеджер'),
    ('Авторизованный клиент');

-- --------------------------------------------------------
-- Справочник статусов заказа
-- --------------------------------------------------------
INSERT INTO order_statuses (name) VALUES
    ('Новый'),
    ('Завершен');

-- --------------------------------------------------------
-- Единицы измерения (из Tovar.xlsx)
-- --------------------------------------------------------
INSERT INTO units (name) VALUES
    ('шт.');

-- --------------------------------------------------------
-- Категории товаров (из Tovar.xlsx)
-- --------------------------------------------------------
INSERT INTO categories (name) VALUES
    ('Общестроительные материалы'),
    ('Стеновые и фасадные материалы'),
    ('Сухие строительные смеси и гидроизоляция'),
    ('Ручной инструмент'),
    ('Защита лица, глаз, головы');

-- --------------------------------------------------------
-- Поставщики (из Tovar.xlsx)
-- --------------------------------------------------------
INSERT INTO suppliers (name) VALUES
    ('М500'),
    ('Изостронг'),
    ('Knauf'),
    ('MixMaster'),
    ('ЛСР'),
    ('ВОЛМА'),
    ('Vinylon'),
    ('Павловский завод'),
    ('Weber'),
    ('Hesler'),
    ('Armero'),
    ('Wenzo Roma'),
    ('KILIMGRIN'),
    ('Исток'),
    ('RUIZ'),
    ('Husqvarna'),
    ('Delta');

-- --------------------------------------------------------
-- Производители (из Tovar.xlsx)
-- (совпадают с поставщиками в данном наборе)
-- --------------------------------------------------------
INSERT INTO manufacturers (name) VALUES
    ('М500'),
    ('Изостронг'),
    ('Knauf'),
    ('MixMaster'),
    ('ЛСР'),
    ('ВОЛМА'),
    ('Vinylon'),
    ('Павловский завод'),
    ('Weber'),
    ('Hesler'),
    ('Armero'),
    ('Wenzo Roma'),
    ('KILIMGRIN'),
    ('Исток'),
    ('RUIZ'),
    ('Husqvarna'),
    ('Delta');

-- --------------------------------------------------------
-- Товары (из Tovar.xlsx)
-- --------------------------------------------------------
INSERT INTO products
    (article, name, unit_id, price, supplier_id, manufacturer_id, category_id,
     discount, stock_quantity, description, photo)
VALUES
-- Общестроительные материалы
('PMEZMH', 'Цемент', 1,
 440, (SELECT id FROM suppliers WHERE name='М500'),
      (SELECT id FROM manufacturers WHERE name='М500'),
      (SELECT id FROM categories WHERE name='Общестроительные материалы'),
 8, 34,
 'Цемент Евроцемент М500 Д0 ЦЕМ I 42,5 50 кг', 'PMEZMH.jpg'),

('BPV4MM', 'Пленка техническая', 1,
 8, (SELECT id FROM suppliers WHERE name='Изостронг'),
    (SELECT id FROM manufacturers WHERE name='Изостронг'),
    (SELECT id FROM categories WHERE name='Общестроительные материалы'),
 8, 2,
 'Пленка техническая полиэтиленовая Изостронг 60 мк 3 м рукав 1,5 м, пог.м', 'BPV4MM.jpg'),

('JVL42J', 'Пленка техническая', 1,
 13, (SELECT id FROM suppliers WHERE name='Изостронг'),
     (SELECT id FROM manufacturers WHERE name='Изостронг'),
     (SELECT id FROM categories WHERE name='Общестроительные материалы'),
 4, 34,
 'Пленка техническая полиэтиленовая Изостронг 100 мк 3 м рукав 1,5 м, пог.м', 'JVL42J.jpg'),

('F895RB', 'Песок строительный', 1,
 102, (SELECT id FROM suppliers WHERE name='Knauf'),
      (SELECT id FROM manufacturers WHERE name='Knauf'),
      (SELECT id FROM categories WHERE name='Общестроительные материалы'),
 6, 7,
 'Песок строительный 50 кг', 'F895RB.jpg'),

('3XBOTN', 'Керамзит фракция', 1,
 110, (SELECT id FROM suppliers WHERE name='MixMaster'),
      (SELECT id FROM manufacturers WHERE name='MixMaster'),
      (SELECT id FROM categories WHERE name='Общестроительные материалы'),
 5, 21,
 'Керамзит фракция 10-20 мм 0,05 куб.м', '3XBOTN.jpeg'),

-- Стеновые и фасадные материалы
('3L7RCZ', 'Газобетон', 1,
 7400, (SELECT id FROM suppliers WHERE name='ЛСР'),
       (SELECT id FROM manufacturers WHERE name='ЛСР'),
       (SELECT id FROM categories WHERE name='Стеновые и фасадные материалы'),
 2, 20,
 'Газобетон ЛСР 100х250х625 мм D400', '3L7RCZ.jpg'),

('S72AM3', 'Пазогребневая плита', 1,
 500, (SELECT id FROM suppliers WHERE name='ВОЛМА'),
      (SELECT id FROM manufacturers WHERE name='ВОЛМА'),
      (SELECT id FROM categories WHERE name='Стеновые и фасадные материалы'),
 5, 35,
 'Пазогребневая плита ВОЛМА Гидро 667х500х80 мм полнотелая', 'S72AM3.jpg'),

('2G3280', 'Угол наружный', 1,
 795, (SELECT id FROM suppliers WHERE name='Vinylon'),
      (SELECT id FROM manufacturers WHERE name='Vinylon'),
      (SELECT id FROM categories WHERE name='Стеновые и фасадные материалы'),
 9, 20,
 'Угол наружный Vinylon 3050 мм серо-голубой', '2G3280.jpg'),

('MIO8YV', 'Кирпич', 1,
 30, (SELECT id FROM suppliers WHERE name='ВОЛМА'),
     (SELECT id FROM manufacturers WHERE name='ВОЛМА'),
     (SELECT id FROM categories WHERE name='Стеновые и фасадные материалы'),
 9, 31,
 'Кирпич рядовой Боровичи полнотелый М150 250х120х65 мм 1NF', 'MIO8YV.jpg'),

('UER2QD', 'Скоба для пазогребневой плиты', 1,
 25, (SELECT id FROM suppliers WHERE name='Knauf'),
     (SELECT id FROM manufacturers WHERE name='Knauf'),
     (SELECT id FROM categories WHERE name='Стеновые и фасадные материалы'),
 8, 27,
 'Скоба для пазогребневой плиты Knauf С1 120х100 мм', 'UER2QD.jpg'),

('ZR70B4', 'Кирпич', 1,
 16, (SELECT id FROM suppliers WHERE name='Павловский завод'),
     (SELECT id FROM manufacturers WHERE name='Павловский завод'),
     (SELECT id FROM categories WHERE name='Стеновые и фасадные материалы'),
 3, 0,
 'Кирпич рядовой силикатный Павловский завод полнотелый М200 250х120х65 мм 1NF', NULL),

-- Сухие строительные смеси и гидроизоляция
('LPDDM4', 'Штукатурка гипсовая', 1,
 500, (SELECT id FROM suppliers WHERE name='Knauf'),
      (SELECT id FROM manufacturers WHERE name='Knauf'),
      (SELECT id FROM categories WHERE name='Сухие строительные смеси и гидроизоляция'),
 6, 38,
 'Штукатурка гипсовая Knauf Ротбанд 30 кг', NULL),

('LQ48MW', 'Штукатурка гипсовая', 1,
 462, (SELECT id FROM suppliers WHERE name='Weber'),
      (SELECT id FROM manufacturers WHERE name='Weber'),
      (SELECT id FROM categories WHERE name='Сухие строительные смеси и гидроизоляция'),
 6, 33,
 'Штукатурка гипсовая Knauf МП-75 машинная 30 кг', NULL),

('O43COU', 'Шпаклевка', 1,
 750, (SELECT id FROM suppliers WHERE name='ВОЛМА'),
      (SELECT id FROM manufacturers WHERE name='ВОЛМА'),
      (SELECT id FROM categories WHERE name='Сухие строительные смеси и гидроизоляция'),
 1, 16,
 'Шпаклевка полимерная Weber.vetonit LR + для сухих помещений белая 20 кг', NULL),

('M26EXW', 'Клей для плитки, керамогранита и камня', 1,
 340, (SELECT id FROM suppliers WHERE name='Knauf'),
      (SELECT id FROM manufacturers WHERE name='Knauf'),
      (SELECT id FROM categories WHERE name='Сухие строительные смеси и гидроизоляция'),
 8, 0,
 'Клей для плитки, керамогранита и камня Крепс Усиленный серый (класс С1) 25 кг', NULL),

('K0YACK', 'Смесь цементно-песчаная', 1,
 160, (SELECT id FROM suppliers WHERE name='MixMaster'),
      (SELECT id FROM manufacturers WHERE name='MixMaster'),
      (SELECT id FROM categories WHERE name='Сухие строительные смеси и гидроизоляция'),
 8, 19,
 'Смесь цементно-песчаная (ЦПС) 300 по ТУ MixMaster Универсал 25 кг', NULL),

('ASPXSG', 'Ровнитель', 1,
 711, (SELECT id FROM suppliers WHERE name='Weber'),
      (SELECT id FROM manufacturers WHERE name='Weber'),
      (SELECT id FROM categories WHERE name='Сухие строительные смеси и гидроизоляция'),
 10, 20,
 'Ровнитель (наливной пол) финишный Weber.vetonit 4100 самовыравнивающийся высокопрочный 20 кг', NULL),

-- Ручной инструмент
('ZKQ5FF', 'Лезвие для ножа', 1,
 65, (SELECT id FROM suppliers WHERE name='Hesler'),
     (SELECT id FROM manufacturers WHERE name='Hesler'),
     (SELECT id FROM categories WHERE name='Ручной инструмент'),
 6, 6,
 'Лезвие для ножа Hesler 18 мм прямое (10 шт.)', NULL),

('4WZEOT', 'Лезвие для ножа', 1,
 110, (SELECT id FROM suppliers WHERE name='Armero'),
      (SELECT id FROM manufacturers WHERE name='Armero'),
      (SELECT id FROM categories WHERE name='Ручной инструмент'),
 6, 17,
 'Лезвие для ножа Armero 18 мм прямое (10 шт.)', NULL),

('4JR1HN', 'Шпатель', 1,
 26, (SELECT id FROM suppliers WHERE name='Hesler'),
     (SELECT id FROM manufacturers WHERE name='Hesler'),
     (SELECT id FROM categories WHERE name='Ручной инструмент'),
 6, 7,
 'Шпатель малярный 100 мм с пластиковой ручкой', NULL),

('Z3XFSP', 'Нож строительный', 1,
 63, (SELECT id FROM suppliers WHERE name='Hesler'),
     (SELECT id FROM manufacturers WHERE name='Hesler'),
     (SELECT id FROM categories WHERE name='Ручной инструмент'),
 8, 5,
 'Нож строительный Hesler 18 мм с ломающимся лезвием пластиковый корпус', NULL),

('I6MH89', 'Валик', 1,
 326, (SELECT id FROM suppliers WHERE name='Wenzo Roma'),
      (SELECT id FROM manufacturers WHERE name='Wenzo Roma'),
      (SELECT id FROM categories WHERE name='Ручной инструмент'),
 12, 3,
 'Валик Wenzo Roma полиакрил 250 мм ворс 18 мм для красок грунтов и антисептиков на водной основе с рукояткой', NULL),

('83M5ME', 'Кисть', 1,
 122, (SELECT id FROM suppliers WHERE name='Armero'),
      (SELECT id FROM manufacturers WHERE name='Armero'),
      (SELECT id FROM categories WHERE name='Ручной инструмент'),
 9, 26,
 'Кисть плоская смешанная щетина 100х12 мм для красок и антисептиков на водной основе', NULL),

-- Защита лица, глаз, головы
('61PGH3', 'Очки защитные', 1,
 184, (SELECT id FROM suppliers WHERE name='KILIMGRIN'),
      (SELECT id FROM manufacturers WHERE name='KILIMGRIN'),
      (SELECT id FROM categories WHERE name='Защита лица, глаз, головы'),
 6, 25,
 'Очки защитные Delta Plus KILIMANDJARO (KILIMGRIN) открытые с прозрачными линзами', NULL),

('GN6ICZ', 'Каска защитная', 1,
 154, (SELECT id FROM suppliers WHERE name='Исток'),
      (SELECT id FROM manufacturers WHERE name='Исток'),
      (SELECT id FROM categories WHERE name='Защита лица, глаз, головы'),
 15, 8,
 'Каска защитная Исток (КАС001О) оранжевая', NULL),

('Z3LO0U', 'Очки защитные', 1,
 228, (SELECT id FROM suppliers WHERE name='RUIZ'),
      (SELECT id FROM manufacturers WHERE name='RUIZ'),
      (SELECT id FROM categories WHERE name='Защита лица, глаз, головы'),
 9, 11,
 'Очки защитные Delta Plus RUIZ (RUIZ1VI) закрытые с прозрачными линзами', NULL),

('QHNOKR', 'Маска защитная', 1,
 251, (SELECT id FROM suppliers WHERE name='Исток'),
      (SELECT id FROM manufacturers WHERE name='Исток'),
      (SELECT id FROM categories WHERE name='Защита лица, глаз, головы'),
 2, 22,
 'Маска защитная Исток (ЩИТ001) ударопрочная и термостойкая', NULL),

('EQ6RKO', 'Подшлемник', 1,
 36, (SELECT id FROM suppliers WHERE name='Husqvarna'),
     (SELECT id FROM manufacturers WHERE name='Husqvarna'),
     (SELECT id FROM categories WHERE name='Защита лица, глаз, головы'),
 17, 22,
 'Подшлемник для каски одноразовый', NULL),

('81F1WG', 'Каска защитная', 1,
 1500, (SELECT id FROM suppliers WHERE name='Delta'),
       (SELECT id FROM manufacturers WHERE name='Delta'),
       (SELECT id FROM categories WHERE name='Защита лица, глаз, головы'),
 2, 13,
 'Каска защитная Delta Plus BASEBALL DIAMOND V UP (DIAM5UPBCFLBS) белая', NULL),

('0YGHZ7', 'Очки защитные', 1,
 700, (SELECT id FROM suppliers WHERE name='Husqvarna'),
      (SELECT id FROM manufacturers WHERE name='Husqvarna'),
      (SELECT id FROM categories WHERE name='Защита лица, глаз, головы'),
 9, 36,
 'Очки защитные Husqvarna Clear (5449638-01) открытые с прозрачными линзами', NULL);

-- --------------------------------------------------------
-- Пользователи (из user_import.xlsx)
-- --------------------------------------------------------
INSERT INTO users (role_id, full_name, login, password) VALUES
-- Администраторы
((SELECT id FROM roles WHERE name='Администратор'),
 'Ворсин Петр Евгеньевич',     '94d5ous@gmail.com',      'uzWC67'),
((SELECT id FROM roles WHERE name='Администратор'),
 'Старикова Елена Павловна',   'uth4iz@mail.com',         '2L6KZG'),
((SELECT id FROM roles WHERE name='Администратор'),
 'Одинцов Серафим Артёмович',  'yzls62@outlook.com',      'JlFRCZ'),
-- Менеджеры
((SELECT id FROM roles WHERE name='Менеджер'),
 'Степанов Михаил Артёмович',  '1diph5e@tutanota.com',    '8ntwUp'),
((SELECT id FROM roles WHERE name='Менеджер'),
 'Ворсин Петр Евгеньевич',     'tjde7c@yahoo.com',        'YOyhfR'),
((SELECT id FROM roles WHERE name='Менеджер'),
 'Старикова Елена Павловна',   'wpmrc3do@tutanota.com',   'RSbvHv'),
-- Авторизованные клиенты
((SELECT id FROM roles WHERE name='Авторизованный клиент'),
 'Михайлюк Анна Вячеславовна',     '5d4zbu@tutanota.com', 'rwVDh9'),
((SELECT id FROM roles WHERE name='Авторизованный клиент'),
 'Ситдикова Елена Анатольевна',    'ptec8ym@yahoo.com',   'LdNyos'),
((SELECT id FROM roles WHERE name='Авторизованный клиент'),
 'Никифорова Весения Николаевна',  '1qz4kw@mail.com',     'gynQMT'),
((SELECT id FROM roles WHERE name='Авторизованный клиент'),
 'Сазонов Руслан Германович',      '4np6se@mail.com',     'AtnDjr');

-- --------------------------------------------------------
-- Пункты выдачи (из Пункты выдачи_import.xlsx)
-- --------------------------------------------------------
INSERT INTO pickup_points (address) VALUES
('420151, г. Лесной, ул. Вишневая, 32'),
('125061, г. Лесной, ул. Подгорная, 8'),
('630370, г. Лесной, ул. Шоссейная, 24'),
('400562, г. Лесной, ул. Зеленая, 32'),
('614510, г. Лесной, ул. Маяковского, 47'),
('410542, г. Лесной, ул. Светлая, 46'),
('620839, г. Лесной, ул. Цветочная, 8'),
('443890, г. Лесной, ул. Коммунистическая, 1'),
('603379, г. Лесной, ул. Спортивная, 46'),
('603721, г. Лесной, ул. Гоголя, 41'),
('410172, г. Лесной, ул. Северная, 13'),
('614611, г. Лесной, ул. Молодежная, 50'),
('454311, г. Лесной, ул. Новая, 19'),
('660007, г. Лесной, ул. Октябрьская, 19'),
('603036, г. Лесной, ул. Садовая, 4'),
('394060, г. Лесной, ул. Фрунзе, 43'),
('410661, г. Лесной, ул. Школьная, 50'),
('625590, г. Лесной, ул. Коммунистическая, 20'),
('625683, г. Лесной, ул. 8 Марта'),
('450983, г. Лесной, ул. Комсомольская, 26'),
('394782, г. Лесной, ул. Чехова, 3'),
('603002, г. Лесной, ул. Дзержинского, 28'),
('450558, г. Лесной, ул. Набережная, 30'),
('344288, г. Лесной, ул. Чехова, 1'),
('614164, г. Лесной, ул. Степная, 30'),
('394242, г. Лесной, ул. Коммунистическая, 43'),
('660540, г. Лесной, ул. Солнечная, 25'),
('125837, г. Лесной, ул. Шоссейная, 40'),
('125703, г. Лесной, ул. Партизанская, 49'),
('625283, г. Лесной, ул. Победы, 46'),
('614753, г. Лесной, ул. Полевая, 35'),
('426030, г. Лесной, ул. Маяковского, 44'),
('450375, г. Лесной, ул. Клубная, 44'),
('625560, г. Лесной, ул. Некрасова, 12'),
('630201, г. Лесной, ул. Комсомольская, 17'),
('190949, г. Лесной, ул. Мичурина, 26');

-- --------------------------------------------------------
-- Заказы (из Заказ_import.xlsx)
-- Примечание: заказ 7 содержит ошибочную дату 30.02.2025
-- (такой даты не существует) → используем 28.02.2025
-- Примечание: артикул O43COU8 в заказе 7 исправлен на O43COU
-- --------------------------------------------------------
INSERT INTO orders
    (order_number, order_date, delivery_date, pickup_point_id, user_id, pickup_code, status_id)
VALUES
(1, '2025-02-27', '2025-04-20',
 1,
 (SELECT id FROM users WHERE full_name='Михайлюк Анна Вячеславовна' LIMIT 1),
 901,
 (SELECT id FROM order_statuses WHERE name='Завершен')),

(2, '2024-09-28', '2025-04-21',
 11,
 (SELECT id FROM users WHERE full_name='Ситдикова Елена Анатольевна' LIMIT 1),
 902,
 (SELECT id FROM order_statuses WHERE name='Завершен')),

(3, '2025-03-21', '2025-04-22',
 2,
 (SELECT id FROM users WHERE full_name='Никифорова Весения Николаевна' LIMIT 1),
 903,
 (SELECT id FROM order_statuses WHERE name='Завершен')),

(4, '2025-02-20', '2025-04-23',
 11,
 (SELECT id FROM users WHERE full_name='Сазонов Руслан Германович' LIMIT 1),
 904,
 (SELECT id FROM order_statuses WHERE name='Завершен')),

(5, '2025-03-17', '2025-04-24',
 2,
 (SELECT id FROM users WHERE full_name='Михайлюк Анна Вячеславовна' LIMIT 1),
 905,
 (SELECT id FROM order_statuses WHERE name='Завершен')),

(6, '2025-03-01', '2025-04-25',
 15,
 (SELECT id FROM users WHERE full_name='Ситдикова Елена Анатольевна' LIMIT 1),
 906,
 (SELECT id FROM order_statuses WHERE name='Завершен')),

-- заказ 7: дата 30.02.2025 не существует → 28.02.2025
(7, '2025-02-28', '2025-04-26',
 3,
 (SELECT id FROM users WHERE full_name='Никифорова Весения Николаевна' LIMIT 1),
 907,
 (SELECT id FROM order_statuses WHERE name='Завершен')),

(8, '2025-03-31', '2025-04-27',
 19,
 (SELECT id FROM users WHERE full_name='Сазонов Руслан Германович' LIMIT 1),
 908,
 (SELECT id FROM order_statuses WHERE name='Новый')),

(9, '2025-04-02', '2025-04-28',
 5,
 (SELECT id FROM users WHERE full_name='Никифорова Весения Николаевна' LIMIT 1),
 909,
 (SELECT id FROM order_statuses WHERE name='Новый')),

(10, '2025-04-03', '2025-04-29',
 19,
 (SELECT id FROM users WHERE full_name='Сазонов Руслан Германович' LIMIT 1),
 910,
 (SELECT id FROM order_statuses WHERE name='Новый'));

-- --------------------------------------------------------
-- Состав заказов (нормализованная таблица order_items)
-- Источник: столбец "Артикул заказа" формата "арт, кол, арт, кол"
-- --------------------------------------------------------
INSERT INTO order_items (order_id, article, quantity) VALUES
-- Заказ 1: PMEZMH,2 + BPV4MM,2
((SELECT id FROM orders WHERE order_number=1), 'PMEZMH', 2),
((SELECT id FROM orders WHERE order_number=1), 'BPV4MM', 2),
-- Заказ 2: JVL42J,1 + F895RB,1
((SELECT id FROM orders WHERE order_number=2), 'JVL42J', 1),
((SELECT id FROM orders WHERE order_number=2), 'F895RB', 1),
-- Заказ 3: 3XBOTN,10 + 3L7RCZ,10
((SELECT id FROM orders WHERE order_number=3), '3XBOTN', 10),
((SELECT id FROM orders WHERE order_number=3), '3L7RCZ', 10),
-- Заказ 4: S72AM3,5 + 2G3280,4
((SELECT id FROM orders WHERE order_number=4), 'S72AM3', 5),
((SELECT id FROM orders WHERE order_number=4), '2G3280', 4),
-- Заказ 5: MIO8YV,2 + UER2QD,2
((SELECT id FROM orders WHERE order_number=5), 'MIO8YV', 2),
((SELECT id FROM orders WHERE order_number=5), 'UER2QD', 2),
-- Заказ 6: ZR70B4,1 + LPDDM4,1
((SELECT id FROM orders WHERE order_number=6), 'ZR70B4', 1),
((SELECT id FROM orders WHERE order_number=6), 'LPDDM4', 1),
-- Заказ 7: LQ48MW,10 + O43COU,10 (исправлен артикул O43COU8 → O43COU)
((SELECT id FROM orders WHERE order_number=7), 'LQ48MW', 10),
((SELECT id FROM orders WHERE order_number=7), 'O43COU', 10),
-- Заказ 8: M26EXW,5 + K0YACK,4
((SELECT id FROM orders WHERE order_number=8), 'M26EXW', 5),
((SELECT id FROM orders WHERE order_number=8), 'K0YACK', 4),
-- Заказ 9: ASPXSG,5 + ZKQ5FF,1
((SELECT id FROM orders WHERE order_number=9), 'ASPXSG', 5),
((SELECT id FROM orders WHERE order_number=9), 'ZKQ5FF', 1),
-- Заказ 10: 4WZEOT,5 + 4JR1HN,5
((SELECT id FROM orders WHERE order_number=10), '4WZEOT', 5),
((SELECT id FROM orders WHERE order_number=10), '4JR1HN', 5);
