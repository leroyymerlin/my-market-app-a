package ru.yandex.practicum.mymarket.repository;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.mymarket.model.Item;

import java.util.List;

/**
 * Репозиторий для работы в странице корзины
 */
@Repository
public interface CartRepository {

    /**
     * Получение страницы со списком товаров в корзине.
     *
     * @return Список товаров.
     */
    List<Item> getCartItems();

    /**
     * Получение общей суммы корзины.
     *
     * @return сумма товаров в корзине.
     */
    int getTotal();

    /**
     * Уменьшение/увеличение количества товаров в корзине.
     *
     * @param id идентификатор товара.
     * @param action действие с товаром - уменьшение/увеличение/удаление
     * @return список товаров.
     */
    List<Item> getIncreaseOrDecreaseCartItem(Long id, String action);
}
