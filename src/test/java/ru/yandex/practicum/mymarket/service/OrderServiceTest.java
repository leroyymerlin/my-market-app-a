package ru.yandex.practicum.mymarket.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.model.Order;
import ru.yandex.practicum.mymarket.repository.CartRepository;
import ru.yandex.practicum.mymarket.repository.ItemRepository;
import ru.yandex.practicum.mymarket.repository.OrderItemRepository;
import ru.yandex.practicum.mymarket.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {"spring.main.lazy-initialization=true"})
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private ItemRepository itemRepository;

    @MockitoBean
    private CartRepository cartRepository;

    @MockitoBean
    private OrderItemRepository orderItemRepository;

    @BeforeEach
    void setUp() {

        Item item1 = new Item();
        item1.setId(1L);
        item1.setTitle("Товар 1");
        item1.setPrice(100L);
        item1.setCount(2);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setTitle("Товар 2");
        item2.setPrice(200L);
        item2.setCount(1);

        Item item3 = new Item();
        item3.setId(3L);
        item3.setTitle("Товар 3");
        item3.setPrice(50L);
        item3.setCount(0);

        when(cartRepository.findByCountGreaterThan(0)).thenReturn(Flux.just(item1, item2));

        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order order = inv.getArgument(0);
            order.setId(1L);
            return Mono.just(order);
        });

        when(orderItemRepository.saveAll(any(Publisher.class))).thenReturn(Flux.empty());

        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        when(orderRepository.findById(1L)).thenReturn(Mono.just(new Order(1L, LocalDateTime.now(), null, 400L)));

        when(orderRepository.findAll()).thenReturn(Flux.just(
                new Order(1L, LocalDateTime.now(), null, 500L),
                new Order(2L, LocalDateTime.now(), null, 1000L),
                new Order(3L, LocalDateTime.now(), null, 5000L)
        ));
    }

    @Test
    void getOrders() {
        orderService.getOrders()
                .as(StepVerifier::create)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void getNewOrder() {
        orderService.getNewOrder(1L)
                .as(StepVerifier::create)
                .expectNextMatches(o -> o.getTotalSum() == 400L)
                .verifyComplete();
    }

    @Test
    void createOrderFromCart() {
        orderService.createOrderFromCart()
                .as(StepVerifier::create)
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();
    }
}