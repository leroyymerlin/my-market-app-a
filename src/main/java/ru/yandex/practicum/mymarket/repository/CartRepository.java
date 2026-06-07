package ru.yandex.practicum.mymarket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mymarket.model.Item;

import java.util.List;

/**
 * Репозиторий для работы в странице корзины
 */
@Repository
public interface CartRepository extends JpaRepository<Item, Long> {

    /**
     * Получение страницы со списком товаров в корзине.
     *
     * @return Список товаров.
     */
    List<Item> findByCountGreaterThan(int count);

    /**
     * Получение общей суммы корзины.
     *
     * @return сумма товаров в корзине.
     */
    @Query("SELECT SUM(i.price * i.count) FROM Item i WHERE i.count > 0")
    Long getTotal();

    /**
     * Увеличение количества товаров в корзине.
     *
     * @param id идентификатор товара.
     */
    @Transactional
    @Modifying
    @Query("UPDATE Item i SET i.count = i.count + 1 WHERE i.id = :id")
    void incrementCount(@Param("id") Long id);

    /**
     * уменьшение количества товаров в корзине.
     *
     * @param id идентификатор товара.
     */
    @Transactional
    @Modifying
    @Query("UPDATE Item i SET i.count = i.count - 1 WHERE i.id = :id AND i.count > 0")
    void decrementCount(@Param("id") Long id);

    /**
     * Удаление товаров в корзине.
     *
     * @param id идентификатор товара.
     */
    @Transactional
    @Modifying
    @Query("UPDATE Item i SET i.count = 0 WHERE i.id = :id")
    void deleteFromCart(@Param("id") Long id);
}
