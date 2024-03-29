DROP TABLE IF EXISTS `ORDER`;

CREATE TABLE `ORDER`
(
    order_id      INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    product_name  VARCHAR,
    product_price INT UNSIGNED,
    created_at    DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at    DATETIME NULL
);
