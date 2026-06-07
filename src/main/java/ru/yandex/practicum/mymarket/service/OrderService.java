package ru.yandex.practicum.mymarket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.model.Order;
import ru.yandex.practicum.mymarket.model.OrderItem;
import ru.yandex.practicum.mymarket.repository.CartRepository;
import ru.yandex.practicum.mymarket.repository.ItemRepository;
import ru.yandex.practicum.mymarket.repository.OrderItemRepository;
import ru.yandex.practicum.mymarket.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional(readOnly = true)
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Order getNewOrder(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Не удалось найти запись"));
    }

    public Long createOrderFromCart() {
        List<Item> cartItems = cartRepository.findByCountGreaterThan(0);

        long totalSum = cartItems.stream()
                .mapToLong(item -> item.getPrice() * item.getCount())
                .sum();

        Order order = new Order();
        order.setTotalSum(totalSum);
        order.setCreatedAt(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);
        Long orderId = savedOrder.getId();

        List<OrderItem> orderItems = new ArrayList<>();
        for (Item item : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setItem(item);
            orderItem.setCount(item.getCount());
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);
        orderItemRepository.saveAll(orderItems);
        for (Item item : cartItems) {
            item.setCount(0);
            itemRepository.save(item);
        }

        return orderId;
    }
}
