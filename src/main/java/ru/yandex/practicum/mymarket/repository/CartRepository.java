package ru.yandex.practicum.mymarket.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.model.Item;

/**
 * Репозиторий для работы в странице корзины
 */
@Repository
public interface CartRepository extends R2dbcRepository<Item, Long> {

    /**
     * Получение страницы со списком товаров в корзине.
     *
     * @return Список товаров.
     */
    Flux<Item> findByCountGreaterThan(int count);

    /**
     * Получение общей суммы корзины.
     *
     * @return сумма товаров в корзине.
     */
    @Query("SELECT SUM(price * count) FROM items WHERE count > 0")
    Mono<Long> getTotal();

    /**
     * Увеличение количества товаров в корзине.
     *
     * @param id идентификатор товара.
     */
    @Transactional
    @Modifying
    @Query("UPDATE items SET count = count + 1 WHERE id = :id")
    Mono<Void> incrementCount(@Param("id") Long id);

    /**
     * уменьшение количества товаров в корзине.
     *
     * @param id идентификатор товара.
     */
    @Transactional
    @Modifying
    @Query("UPDATE items SET count = count - 1 WHERE id = :id AND count > 0")
    Mono<Void> decrementCount(@Param("id") Long id);

    /**
     * Удаление товаров в корзине.
     *
     * @param id идентификатор товара.
     */
    @Transactional
    @Modifying
    @Query("UPDATE items SET count = 0 WHERE id = :id")
    Mono<Void> deleteFromCart(@Param("id") Long id);
}
