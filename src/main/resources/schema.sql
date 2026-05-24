
create table if not exists items (
    id BIGINT primary key,
    title text not null,
    description text,
    img_path text,
    price BIGINT,
    image_data LONGBLOB,
    page_number integer,
    count integer
);

create table if not exists orders (
    id BIGINT primary key,
    total_sum BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_items (
    order_id BIGINT,
    item_id BIGINT,
    count INT NOT NULL,
    PRIMARY KEY (order_id, item_id),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (item_id) REFERENCES items(id)
);