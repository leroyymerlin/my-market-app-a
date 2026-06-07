package ru.yandex.practicum.mymarket.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.model.Order;
import ru.yandex.practicum.mymarket.model.OrderItem;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Transactional
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findByIdWithItems() {
        orderRepository.deleteAll();
        itemRepository.deleteAll();

        Item item1 = new Item();
        item1.setId(1L);
        item1.setTitle("Товар 1");
        item1.setPrice(100L);
        item1.setCount(0);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setTitle("Товар 2");
        item2.setPrice(200L);
        item2.setCount(0);
        itemRepository.save(item2);

        Order order = new Order();
        order.setTotalSum(500L);
        order.setCreatedAt(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);
        Long orderId = savedOrder.getId();

        OrderItem oi1 = new OrderItem();
        oi1.setOrder(savedOrder);
        oi1.setItem(item1);
        oi1.setCount(1);

        OrderItem oi2 = new OrderItem();
        oi2.setOrder(savedOrder);
        oi2.setItem(item2);
        oi2.setCount(2);

        Optional<Order> orderOpt = orderRepository.findByIdWithItems(orderId);
        assertThat(orderOpt).isPresent();

        Order orderTest = orderOpt.get();
        orderTest.getOrderItems().forEach(oi -> {
            assertThat(oi.getItem().getTitle()).isIn("Товар 1", "Товар 2");
            assertThat(oi.getCount()).isPositive();
        });
    }
}