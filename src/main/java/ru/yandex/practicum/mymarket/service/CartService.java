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
        return cartRepository.findByCountGreaterThan(0);
    }

    public Long getTotal() {
        return cartRepository.getTotal();
    }

    public List<Item> increaseOrDecreaseCartItem(Long id, String action) {
        switch (action.toUpperCase()) {
            case "PLUS": cartRepository.incrementCount(id); break;
            case "MINUS": cartRepository.decrementCount(id); break;
            case "DELETE": cartRepository.deleteFromCart(id); break;
            default: throw new IllegalArgumentException("Неизвестное действие: " + action);
        }
        return getCartItems();
    }
}
