package ru.yandex.practicum.mymarket.repository;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.model.Paging;

import java.util.List;

/**
 * Репозиторий по работе со страницей магазина.
 */
@Repository
public interface MarketRepository {

    /**
     * Получение товаров на странице магазина.
     *
     * @param search строка поиска товара.
     * @param sort способ сортировки товсара.
     * @param pageNumber номер страницы.
     * @param pageSize число товаров на странице.
     * @return список товаров.
     */
    List<Item> getItems(String search, String sort, int pageNumber,int pageSize);

    /**
     * Получение страницы.
     *
     * @param search строка поиска.
     * @param pageNumber номер страницы.
     * @param pageSize число товаров на странице.
     * @return объект-страница.
     */
    Paging getPaging(String search, int pageNumber, int pageSize);

    /**
     * Получение одного товара.
     *
     * @param id идентификатор товара.
     * @return товар.
     */
    Item getItem(Long id);

    /**
     * Уменьшение/увеличение товара.
     *
     * @param id идентификатор товара.
     * @param action действие.
     * @return товар.
     */
    Item getIncreaseOrDecreaseItem(Long id, String action);
}
