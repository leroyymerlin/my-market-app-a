package ru.yandex.practicum.mymarket.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.model.Order;

@Repository
public interface OrderRepository extends R2dbcRepository<Order, Long> {

    @Query("SELECT oi.*, i.* FROM order_items oi JOIN items i ON oi.item_id = i.id WHERE oi.order_id = :orderId")
    Mono<Order> findByIdWithItems(@Param("id") Long id);
}
