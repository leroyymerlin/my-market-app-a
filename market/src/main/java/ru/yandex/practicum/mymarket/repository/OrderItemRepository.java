package ru.yandex.practicum.mymarket.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.mymarket.model.OrderItem;

/**
 * Вспомогательный репозиторий для работы с товарами на страницеэ
 */
@Repository
public interface OrderItemRepository extends R2dbcRepository<OrderItem, Long> {

}
