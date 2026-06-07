package ru.yandex.practicum.mymarket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.model.Order;
import ru.yandex.practicum.mymarket.model.OrderItem;
import ru.yandex.practicum.mymarket.repository.CartRepository;
import ru.yandex.practicum.mymarket.repository.ItemRepository;
import ru.yandex.practicum.mymarket.repository.OrderItemRepository;
import ru.yandex.practicum.mymarket.repository.OrderRepository;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional(readOnly = true)
    public Flux<Order> getOrders() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Mono<Order> getNewOrder(Long id) {
        return  orderRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Не удалось найти запись")));
    }

    public Mono<Long> createOrderFromCart() {
        return cartRepository.findByCountGreaterThan(0)
                .collectList()
                .flatMap(cartItems -> {
                    if (cartItems.isEmpty()) {
                        return Mono.error(new IllegalStateException("Корзина пуста"));
                    }
                    long totalSum = cartItems.stream()
                            .mapToLong(item -> item.getPrice() * item.getCount())
                            .sum();

                    Order order = new Order();
                    order.setTotalSum(totalSum);
                    order.setCreatedAt(LocalDateTime.now());

                    return orderRepository.save(order)
                            .flatMap(savedOrder -> {
                                Flux<OrderItem> orderItemsFlux = Flux.fromIterable(cartItems)
                                        .map(item -> {
                                            OrderItem oi = new OrderItem();
                                            oi.setOrderId(savedOrder.getId());
                                            oi.setItemId(item.getId());
                                            oi.setCount(item.getCount());
                                            return oi;
                                        });
                                return orderItemRepository.saveAll(orderItemsFlux)
                                        .thenMany(Flux.fromIterable(cartItems))
                                        .flatMap(item -> {
                                            item.setCount(0);
                                            return itemRepository.save(item);
                                        })
                                        .then(Mono.just(savedOrder.getId()));
                            });
                });
    }
}
