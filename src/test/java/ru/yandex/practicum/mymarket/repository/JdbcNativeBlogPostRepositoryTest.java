package ru.yandex.practicum.mymarket.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.model.Order;
import ru.yandex.practicum.mymarket.model.Paging;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:test-application.properties")
class JdbcNativeBlogPostRepositoryTest {

    @Autowired
    private JdbcNativeBlogPostRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM order_items");
        jdbcTemplate.execute("DELETE FROM orders");
        jdbcTemplate.execute("DELETE FROM items");

        jdbcTemplate.execute("INSERT INTO items (id, title, description, img_path, price, count) VALUES (1, 'Товар A', 'Описание A', '/img/a.jpg', 100, 2)");
        jdbcTemplate.execute("INSERT INTO items (id, title, description, img_path, price, count) VALUES (2, 'Товар B', 'Описание B', '/img/b.jpg', 200, 0)");
        jdbcTemplate.execute("INSERT INTO items (id, title, description, img_path, price, count) VALUES (3, 'C', 'Описание C', '/img/c.jpg', 50, 5)");
        jdbcTemplate.execute("INSERT INTO items (id, title, description, img_path, price, count) VALUES (4, 'AAA', 'Описание A', '/img/aa.jpg', 300, 1)");

        jdbcTemplate.execute("INSERT INTO orders (id, total_sum, created_at) VALUES (100, 1500, CURRENT_TIMESTAMP)");
        jdbcTemplate.execute("INSERT INTO orders (id, total_sum, created_at) VALUES (101, 2500, CURRENT_TIMESTAMP)");

        jdbcTemplate.execute("INSERT INTO order_items (order_id, item_id, count) VALUES (100, 1, 2)");
        jdbcTemplate.execute("INSERT INTO order_items (order_id, item_id, count) VALUES (100, 3, 1)");
        jdbcTemplate.execute("INSERT INTO order_items (order_id, item_id, count) VALUES (101, 2, 3)");
        jdbcTemplate.execute("INSERT INTO order_items (order_id, item_id, count) VALUES (101, 4, 1)");
    }


    @Test
    void getItems_noSearchNoSort() {
        List<Item> items = repository.getItems(null, null, 1, 10);
        assertThat(items).hasSize(4);
        assertThat(items.get(0).getId()).isEqualTo(1); // сортировка по id по умолчанию
    }

    @Test
    void getItems_withSearch() {
        List<Item> items = repository.getItems("A", null, 1, 10);
        assertThat(items).hasSize(2);
        assertThat(items.get(0).getTitle()).isIn("Товар A", "AAA");
    }

    @Test
    void getItems_sortAlpha() {
        List<Item> items = repository.getItems(null, "ALPHA", 1, 10);
        assertThat(items).hasSize(4);
        assertThat(items.get(0).getTitle()).isEqualTo("AAA");
        assertThat(items.get(1).getTitle()).isEqualTo("C");
        assertThat(items.get(2).getTitle()).isEqualTo("Товар A");
        assertThat(items.get(3).getTitle()).isEqualTo("Товар B");
    }

    @Test
    void getItems_sortPrice() {
        List<Item> items = repository.getItems(null, "PRICE", 1, 10);
        assertThat(items.get(0).getPrice()).isEqualTo(50);  // id=3
        assertThat(items.get(1).getPrice()).isEqualTo(100); // id=1
        assertThat(items.get(2).getPrice()).isEqualTo(200); // id=2
        assertThat(items.get(3).getPrice()).isEqualTo(300); // id=4
    }

    @Test
    void getItems_pagination() {
        List<Item> itemsPage1 = repository.getItems(null, null, 1, 2);
        assertThat(itemsPage1).hasSize(2);
        assertThat(itemsPage1.get(0).getId()).isEqualTo(1);
        assertThat(itemsPage1.get(1).getId()).isEqualTo(2);

        List<Item> itemsPage2 = repository.getItems(null, null, 2, 2);
        assertThat(itemsPage2).hasSize(2);
        assertThat(itemsPage2.get(0).getId()).isEqualTo(3);
        assertThat(itemsPage2.get(1).getId()).isEqualTo(4);
    }

    @Test
    void getPaging_firstPage() {
        Paging paging = repository.getPaging(null, 1, 2);
        assertEquals(1, paging.getPageNumber());
        assertEquals(2, paging.getPageSize());
        assertFalse(paging.isHasPrev());
        assertTrue(paging.isHasNext()); // всего 4 товара, на странице 2 → есть следующая
    }

    @Test
    void getPaging_lastPage() {
        Paging paging = repository.getPaging(null, 2, 2);
        assertTrue(paging.isHasPrev());
        assertFalse(paging.isHasNext());
    }

    @Test
    void getPaging_withSearch() {
        Paging paging = repository.getPaging("A", 1, 2);
        // всего 2 товара с буквой A (id 1 и 4)
        assertFalse(paging.isHasNext()); // 2 товара на странице 2 — следующей нет
        assertEquals(1, paging.getPageNumber());
    }

    @Test
    void getItem_existing() {
        Item item = repository.getItem(1L);
        assertNotNull(item);
        assertEquals("Товар A", item.getTitle());
        assertEquals(100, item.getPrice());
        assertEquals(2, item.getCount());
    }

    @Test
    void getItem_notExisting() {
        assertThatThrownBy(() -> repository.getItem(999L))
                .isInstanceOf(org.springframework.dao.EmptyResultDataAccessException.class);
    }

    @Test
    void getIncreaseOrDecreaseItem_plus() {
        Item updated = repository.getIncreaseOrDecreaseItem(1L, "PLUS");
        assertEquals(3, updated.getCount()); // было 2 → стало 3
        Integer count = jdbcTemplate.queryForObject("SELECT count FROM items WHERE id=1", Integer.class);
        assertEquals(3, count);
    }

    @Test
    void getIncreaseOrDecreaseItem_minus() {
        Item updated = repository.getIncreaseOrDecreaseItem(1L, "MINUS");
        assertEquals(1, updated.getCount()); // было 2 → стало 1
    }

    @Test
    void getIncreaseOrDecreaseItem_minusWhenZero() {
        Item updated = repository.getIncreaseOrDecreaseItem(2L, "MINUS");
        assertEquals(0, updated.getCount()); // не должно уйти в минус
    }

    @Test
    void getCartItems() {
        List<Item> cart = repository.getCartItems();
        assertThat(cart).hasSize(3);
        assertThat(cart).extracting(Item::getId).containsExactlyInAnyOrder(1L, 3L, 4L);
    }

    @Test
    void getTotal() {
        int total = repository.getTotal();
        assertEquals(750, total);
    }

    @Test
    void getIncreaseOrDecreaseCartItem_plus() {
        List<Item> cart = repository.getIncreaseOrDecreaseCartItem(1L, "PLUS");
        assertThat(cart).hasSize(3);
        Item item1 = cart.stream().filter(i -> i.getId() == 1).findFirst().get();
        assertEquals(3, item1.getCount());
        assertEquals(850, repository.getTotal());
    }

    @Test
    void getIncreaseOrDecreaseCartItem_minus() {
        List<Item> cart = repository.getIncreaseOrDecreaseCartItem(1L, "MINUS");
        Item item1 = cart.stream().filter(i -> i.getId() == 1).findFirst().get();
        assertEquals(1, item1.getCount());
    }

    @Test
    void getIncreaseOrDecreaseCartItem_delete() {
        List<Item> cart = repository.getIncreaseOrDecreaseCartItem(1L, "DELETE");
        assertThat(cart).hasSize(2);
        assertThat(cart).extracting(Item::getId).doesNotContain(1L);
        assertEquals(550, repository.getTotal());
    }

    @Test
    void addImage() {
        byte[] imageData = {1, 2, 3};
        repository.addImage(1, imageData, "image/png");
        byte[] stored = jdbcTemplate.queryForObject("SELECT image_data FROM items WHERE id=1", byte[].class);
        assertArrayEquals(imageData, stored);
    }

    @Test
    void getOrders() {
        List<Order> orders = repository.getOrders();
        assertThat(orders).hasSize(2);
        Order order100 = orders.stream().filter(o -> o.getId() == 100).findFirst().get();
        assertEquals(1500, order100.getTotalSum());
        assertThat(order100.getItems()).hasSize(2);
        assertThat(order100.getItems()).extracting(Item::getId).containsExactlyInAnyOrder(1L, 3L);
    }

    @Test
    void getNewOrder_existing() {
        Order order = repository.getNewOrder(100L);
        assertNotNull(order);
        assertEquals(100, order.getId());
        assertEquals(1500, order.getTotalSum());
        assertThat(order.getItems()).hasSize(2);
    }

    @Test
    void getNewOrder_notExisting() {
        assertThatThrownBy(() -> repository.getNewOrder(999L))
                .isInstanceOf(org.springframework.dao.EmptyResultDataAccessException.class);
    }
}