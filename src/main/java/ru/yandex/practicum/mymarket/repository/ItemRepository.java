package ru.yandex.practicum.mymarket.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mymarket.model.Item;

import java.util.List;

/**
 * Репозиторий по работе со страницей магазина.
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findByTitleContainingOrDescriptionContaining(String title, String description, Pageable pageable);

    List<Item> findByCountGreaterThan(int count);

    /**
     * Уменьшение товара.
     *
     * @param id идентификатор товара.
     */
    @Modifying
    @Transactional
    @Query("UPDATE Item i SET i.count = i.count + 1 WHERE i.id = :id")
    void incrementCount(@Param("id") Long id);

    /**
     * Увеличение товара.
     *
     * @param id
     */
    @Modifying
    @Transactional
    @Query("UPDATE Item i SET i.count = i.count - 1 WHERE i.id = :id AND i.count > 0")
    void decrementCount(@Param("id") Long id);
}
