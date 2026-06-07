package ru.yandex.practicum.mymarket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.mymarket.model.OrderItem;

/**
 * Вспомогательный репозиторий для работы с товарами на страницеэ
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
