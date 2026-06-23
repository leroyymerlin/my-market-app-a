
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