package ru.yandex.practicum.mymarket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.repository.CartRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CartService {

    private final CartRepository cartRepository;

    public List<Item> getCartItems() {
        return cartRepository.getCartItems();
    }

    public int getTotal() {
        return cartRepository.getTotal();
    }

    public List<Item> getIncreaseOrDecreaseCartItem(Long id, String action) {
        return cartRepository.getIncreaseOrDecreaseCartItem(id, action);
    }
}
