package ru.yandex.practicum.mymarket.repository;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.mymarket.model.Item;

import java.util.List;

@Repository
public interface CartRepository {

    List<Item> getCartItems();

    int getTotal();

    List<Item> getIncreaseOrDecreaseCartItem(Long id, String action);
}
