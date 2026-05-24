package ru.yandex.practicum.mymarket.repository;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.mymarket.model.Order;

import java.util.List;

@Repository
public interface OrderRepository {

    List<Order> getOrders();

    Order getNewOrder(Long id);
}
