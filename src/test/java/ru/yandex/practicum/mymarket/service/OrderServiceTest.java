package ru.yandex.practicum.mymarket.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.model.Order;
import ru.yandex.practicum.mymarket.repository.CartRepository;
import ru.yandex.practicum.mymarket.repository.ItemRepository;
import ru.yandex.practicum.mymarket.repository.OrderItemRepository;
import ru.yandex.practicum.mymarket.repository.OrderRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest(properties = {"spring.main.lazy-initialization=true"})
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @BeforeEach
    void setUp() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        itemRepository.deleteAll();

        Item item1 = new Item();
        item1.setId(1L);
        item1.setTitle("Товар 1");
        item1.setPrice(100L);
        item1.setCount(2);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setTitle("Товар 2");
        item2.setPrice(200L);
        item2.setCount(1);
        itemRepository.save(item2);

        Item item3 = new Item();
        item3.setId(3L);
        item3.setTitle("Товар 3");
        item3.setPrice(50L);
        item3.setCount(0);
        itemRepository.save(item3);

        itemRepository.flush();
    }

    @Test
    void getOrders() {
        assertThat(orderService.getOrders()).isEmpty();

        Order order1 = new Order();
        order1.setTotalSum(500L);
        order1.setCreatedAt(java.time.LocalDateTime.now());
        orderRepository.save(order1);

        Order order2 = new Order();
        order2.setTotalSum(1000L);
        order2.setCreatedAt(java.time.LocalDateTime.now());
        orderRepository.save(order2);

        List<Order> orders = orderService.getOrders();
        assertThat(orders).hasSize(2);
        assertThat(orders).extracting(Order::getTotalSum).containsExactlyInAnyOrder(500L, 1000L);
    }

    @Test
    void getNewOrder() {
        Order order = new Order();
        order.setTotalSum(300L);
        order.setCreatedAt(java.time.LocalDateTime.now());
        Order saved = orderRepository.save(order);

        Order found = orderService.getNewOrder(saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getTotalSum()).isEqualTo(300L);
    }

    @Test
    void createOrderFromCart() {

        assertThat(cartRepository.findByCountGreaterThan(0)).hasSize(2);

        Long orderId = orderService.createOrderFromCart();
        assertThat(orderId).isNotNull();

        Order order = orderRepository.findById(orderId).orElseThrow();
        assertThat(order.getTotalSum()).isEqualTo(400L);
        assertThat(order.getOrderItems()).hasSize(2);

        assertThat(cartRepository.findByCountGreaterThan(0)).isEmpty();

        assertThat(itemRepository.findById(1L).get().getCount()).isZero();
        assertThat(itemRepository.findById(2L).get().getCount()).isZero();
    }
}