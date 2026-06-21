package ru.yandex.practicum.mymarket.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.model.Order;
import ru.yandex.practicum.mymarket.model.OrderItem;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataR2dbcTest
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @BeforeEach
    void setUp() {
        orderItemRepository.deleteAll()
                .then(orderRepository.deleteAll())
                .then(itemRepository.deleteAll())
                .block();
    }

    @Test
    void findByIdWithItems() {
        Item item1 = new Item();
        item1.setTitle("Товар 1");
        item1.setPrice(100L);
        item1.setCount(0);

        Item item2 = new Item();
        item2.setTitle("Товар 2");
        item2.setPrice(200L);
        item2.setCount(0);

        itemRepository.saveAll(Flux.just(item1, item2))
                .collectList()
                .flatMap(savedItems -> {
                    Item savedItem1 = savedItems.get(0);
                    Item savedItem2 = savedItems.get(1);

                    Order order = new Order(null, LocalDateTime.now(), null, 500L);
                    return orderRepository.save(order)
                            .flatMap(savedOrder -> {
                                OrderItem oi1 = new OrderItem();
                                oi1.setOrderId(savedOrder.getId());
                                oi1.setItemId(savedItem1.getId());
                                oi1.setCount(1);

                                OrderItem oi2 = new OrderItem();
                                oi2.setOrderId(savedOrder.getId());
                                oi2.setItemId(savedItem2.getId());
                                oi2.setCount(2);

                                return orderItemRepository.saveAll(Flux.just(oi1, oi2))
                                        .then(Mono.just(savedOrder));
                            });
                })
                .as(StepVerifier::create)
                .assertNext(savedOrder -> {
                    assertThat(savedOrder.getId()).isNotNull();
                    assertThat(savedOrder.getTotalSum()).isEqualTo(500L);
                })
                .verifyComplete();
    }
}