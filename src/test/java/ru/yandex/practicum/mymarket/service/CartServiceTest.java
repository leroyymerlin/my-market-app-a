package ru.yandex.practicum.mymarket.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.repository.ItemRepository;
import ru.yandex.practicum.mymarket.repository.OrderRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest(properties = {"spring.main.lazy-initialization=true"})
@Transactional
class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    EntityManager entityManager;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        itemRepository.deleteAll();

        Item item1 = new Item();
        item1.setId(1L);
        item1.setTitle("Телефон");
        item1.setPrice(30_000L);
        item1.setCount(2);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setTitle("Наушники");
        item2.setPrice(5_000L);
        item2.setCount(1);
        itemRepository.save(item2);

        Item item3 = new Item();
        item3.setId(3L);
        item3.setTitle("Чехол");
        item3.setPrice(1_000L);
        item3.setCount(0);
        itemRepository.save(item3);
    }

    @Test
    void getCartItems() {
        List<Item> cartItems = cartService.getCartItems();
        assertThat(cartItems).hasSize(2);
        assertThat(cartItems).extracting(Item::getId).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void getTotal() {
        Long total = cartService.getTotal();
        assertThat(total).isEqualTo(65000);
    }

    @Test
    void increaseCartItem_plus() {
        cartService.increaseOrDecreaseCartItem(1L, "PLUS");
        entityManager.clear();

        Item updated = itemRepository.findById(1L).orElseThrow();
        assertThat(updated.getCount()).isEqualTo(3);
        assertThat(cartService.getTotal()).isEqualTo(95000);
    }

    @Test
    void increaseCartItem_minus() {
        cartService.increaseOrDecreaseCartItem(1L, "MINUS");
        entityManager.clear();

        Item updated = itemRepository.findById(1L).orElseThrow();
        assertThat(updated.getCount()).isEqualTo(1);
        assertThat(cartService.getTotal()).isEqualTo(35000);
    }

    @Test
    void increaseCartItem_delete() {
        List<Item> cart = cartService.increaseOrDecreaseCartItem(1L, "DELETE");
        assertThat(cart).hasSize(1);
        assertThat(cart).extracting(Item::getId).containsExactly(2L);
        assertThat(cartService.getTotal()).isEqualTo(5000);
    }
}