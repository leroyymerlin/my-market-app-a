package ru.yandex.practicum.mymarket.repository;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.mymarket.model.Order;

import java.util.List;

/**
 * Репозиторий для работы со списком заказов.
 */
@Repository
public interface OrderRepository {

    /**
     * Получение списка заказов.
     *
     * @return список заказов.
     */
    List<Order> getOrders();

    /**
     * Получение страницы заказа.
     *
     * @param id идентификатор заказа.
     * @return заказ.
     */
    Order getNewOrder(Long id);

    /**
     * Создание страницы заказа.
     *
     * @return идентификатор страницы.
     */
    Long createOrderFromCart();
}
