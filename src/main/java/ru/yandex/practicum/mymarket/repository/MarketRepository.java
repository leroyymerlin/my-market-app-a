package ru.yandex.practicum.mymarket.repository;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.model.Paging;

import java.util.List;

@Repository
public interface MarketRepository {

    List<Item> getItems(String search, String sort, int pageNumber,int pageSize);

    Paging getPaging(String search, int pageNumber, int pageSize);

    Item getItem(Long id);

    Item getIncreaseOrDecreaseItem(Long id, String action);
}
