package ru.yandex.practicum.mymarket.repository;

import org.springframework.data.domain.Sort;
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
 * Репозиторий по работе со страницей магазина.
 */
@Repository
public interface ItemRepository extends R2dbcRepository<Item, Long> {

    /**
     *  Поиск товаров по содержанию в заголовке или описании с сортировкой.
     *
     * @param title заголовок
     * @param description описание
     * @param sort сортировка
     * @return поток товаров.
     */
    Flux<Item> findByTitleContainingOrDescriptionContaining(String title, String description, Sort sort);

    /**
     * Подсчёт количества товаров, удовлетворяющих поисковому запросу
     *
     * @param title заголовок
     * @param description описание
     * @return количество товаров.
     */
    Mono<Long> countByTitleContainingOrDescriptionContaining(String title, String description);

    /**
     * Получение всех товаров, у которых количество больше 0.
     *
     * @param count количество
     * @return товары
     */
    Flux<Item> findByCountGreaterThan(int count);

    /**
     * Уменьшение товара.
     *
     * @param id идентификатор товара.
     */
    @Modifying
    @Transactional
    @Query("UPDATE items SET count = count + 1 WHERE id = :id")
    Mono<Void> incrementCount(@Param("id") Long id);

    /**
     * Увеличение товара.
     *
     * @param id идентификатор товара
     */
    @Modifying
    @Transactional
    @Query("UPDATE items SET count = count - 1 WHERE id = :id AND count > 0")
    Mono<Void> decrementCount(@Param("id") Long id);
}
