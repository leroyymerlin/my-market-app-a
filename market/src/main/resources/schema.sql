
create table if not exists items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title text NOT NULL,
    description text,
    img_path text,
    price BIGINT,
    image_data LONGBLOB,
    page_number integer,
    count integer
);

create table if not exists orders (
    id BIGINT AUTO_INCREMENT primary key,
    total_sum BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT,
    item_id BIGINT,
    count INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (item_id) REFERENCES items(id)
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS authorities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    authority VARCHAR(255) NOT NULL,
    FOREIGN KEY (username) REFERENCES users(username)
);

INSERT INTO users (username, password, enabled) VALUES ('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', true);
INSERT INTO authorities (username, authority) VALUES ('admin', 'ROLE_USER');