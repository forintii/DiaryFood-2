-- ============================================================
-- DairyFood — дамп базы данных course_work
-- Создаёт схему и начальные данные при старте приложения.
-- Повторный запуск безопасен: IF NOT EXISTS / INSERT IGNORE.
-- ============================================================

CREATE TABLE IF NOT EXISTS `users` (
    `id`            VARCHAR(36)  NOT NULL,
    `email`         VARCHAR(255) NOT NULL,
    `password_hash` VARCHAR(255) NOT NULL,
    `created_at`    DATE         NOT NULL,
    `is_active`     TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_users_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `profiles` (
    `id`         VARCHAR(36)  NOT NULL,
    `name`       VARCHAR(255),
    `gender`     VARCHAR(10),
    `birth_date` DATE,
    `goal`       VARCHAR(20),
    `user_id`    VARCHAR(36),
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_profiles_user` (`user_id`),
    CONSTRAINT `fk_profiles_user`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `body_metrics` (
    `id`             VARCHAR(36) NOT NULL,
    `measured_at`    DATETIME(6) NOT NULL,
    `height`         DOUBLE,
    `weight`         DOUBLE,
    `activity_level` VARCHAR(30),
    `user_id`        VARCHAR(36),
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_body_metrics_user`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `products` (
    `id`                  VARCHAR(36)  NOT NULL,
    `name`                VARCHAR(255) NOT NULL,
    `calories_per100g`    DOUBLE,
    `proteins_per100g`    DOUBLE,
    `fats_per100g`        DOUBLE,
    `carbs_per100g`       DOUBLE,
    `is_custom`           TINYINT(1)   NOT NULL DEFAULT 0,
    `created_by_user_id`  VARCHAR(36),
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_products_user`
        FOREIGN KEY (`created_by_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `recipes` (
    `id`                 VARCHAR(36)  NOT NULL,
    `name`               VARCHAR(255) NOT NULL,
    `total_weight`       DOUBLE       DEFAULT 0,
    `calories_per100g`   DOUBLE       DEFAULT 0,
    `proteins_per100g`   DOUBLE       DEFAULT 0,
    `fats_per100g`       DOUBLE       DEFAULT 0,
    `carbs_per100g`      DOUBLE       DEFAULT 0,
    `photo`              LONGBLOB,
    `photo_content_type` VARCHAR(100),
    `created_by`         VARCHAR(36),
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_recipes_user`
        FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `recipe_ingredients` (
    `id`         VARCHAR(36) NOT NULL,
    `weight`     DOUBLE      NOT NULL,
    `recipe_id`  VARCHAR(36) NOT NULL,
    `product_id` VARCHAR(36) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_ri_recipe`
        FOREIGN KEY (`recipe_id`)  REFERENCES `recipes`  (`id`),
    CONSTRAINT `fk_ri_product`
        FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `diary_entries` (
    `id`          VARCHAR(36) NOT NULL,
    `weight`      DOUBLE      NOT NULL,
    `consumed_at` DATE        NOT NULL,
    `meal_type`   VARCHAR(20),
    `user_id`     VARCHAR(36),
    `product_id`  VARCHAR(36),
    `recipe_id`   VARCHAR(36),
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_diary_user`
        FOREIGN KEY (`user_id`)    REFERENCES `users`    (`id`),
    CONSTRAINT `fk_diary_product`
        FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
    CONSTRAINT `fk_diary_recipe`
        FOREIGN KEY (`recipe_id`)  REFERENCES `recipes`  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Начальные данные: база продуктов питания
-- Фиксированные UUID — INSERT IGNORE пропускает при повторном запуске
-- ============================================================

INSERT IGNORE INTO `products`
    (`id`, `name`, `calories_per100g`, `proteins_per100g`, `fats_per100g`, `carbs_per100g`, `is_custom`)
VALUES
    ('df000000-0000-0000-0000-000000000001', 'Куриная грудка (варёная)',  165,  31.0,  3.6,  0.0, 0),
    ('df000000-0000-0000-0000-000000000002', 'Говядина (варёная)',        187,  18.9, 12.4,  0.0, 0),
    ('df000000-0000-0000-0000-000000000003', 'Свинина (тушёная)',         259,  16.0, 21.6,  0.0, 0),
    ('df000000-0000-0000-0000-000000000004', 'Лосось (запечённый)',       208,  20.1, 13.4,  0.0, 0),
    ('df000000-0000-0000-0000-000000000005', 'Яйцо куриное',             157,  12.7, 11.5,  0.7, 0),
    ('df000000-0000-0000-0000-000000000006', 'Творог 5%',                121,  17.2,  5.0,  1.8, 0),
    ('df000000-0000-0000-0000-000000000007', 'Молоко 2.5%',               52,   2.8,  2.5,  4.7, 0),
    ('df000000-0000-0000-0000-000000000008', 'Кефир 1%',                  40,   3.8,  1.0,  4.1, 0),
    ('df000000-0000-0000-0000-000000000009', 'Сыр твёрдый',              402,  25.0, 33.0,  0.0, 0),
    ('df000000-0000-0000-0000-000000000010', 'Гречневая крупа (варёная)', 110,   4.2,  1.1, 21.9, 0),
    ('df000000-0000-0000-0000-000000000011', 'Рис белый (варёный)',       130,   2.7,  0.3, 28.2, 0),
    ('df000000-0000-0000-0000-000000000012', 'Овсяная крупа',             371,  11.9,  7.2, 69.3, 0),
    ('df000000-0000-0000-0000-000000000013', 'Макароны (варёные)',        138,   5.3,  0.8, 27.5, 0),
    ('df000000-0000-0000-0000-000000000014', 'Хлеб пшеничный',           242,   7.7,  2.4, 48.3, 0),
    ('df000000-0000-0000-0000-000000000015', 'Хлеб ржаной',              174,   6.6,  1.2, 34.2, 0),
    ('df000000-0000-0000-0000-000000000016', 'Картофель (варёный)',        77,   2.0,  0.4, 17.0, 0),
    ('df000000-0000-0000-0000-000000000017', 'Морковь',                    41,   0.9,  0.2,  9.6, 0),
    ('df000000-0000-0000-0000-000000000018', 'Капуста белокочанная',       27,   1.8,  0.1,  5.4, 0),
    ('df000000-0000-0000-0000-000000000019', 'Огурец свежий',              15,   0.8,  0.1,  2.8, 0),
    ('df000000-0000-0000-0000-000000000020', 'Помидор',                    20,   0.6,  0.2,  4.2, 0),
    ('df000000-0000-0000-0000-000000000021', 'Банан',                      89,   1.1,  0.3, 22.8, 0),
    ('df000000-0000-0000-0000-000000000022', 'Яблоко',                     52,   0.4,  0.4, 11.8, 0),
    ('df000000-0000-0000-0000-000000000023', 'Апельсин',                   43,   0.9,  0.2,  8.1, 0),
    ('df000000-0000-0000-0000-000000000024', 'Масло сливочное',           717,   0.5, 82.5,  0.5, 0),
    ('df000000-0000-0000-0000-000000000025', 'Масло подсолнечное',        884,   0.0, 99.9,  0.0, 0);
