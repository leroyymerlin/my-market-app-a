package ru.yandex.practicum.mymarket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.repository.CartRepository;

@RequiredArgsConstructor
@Service
public class CartService {

    private final CartRepository cartRepository;

    public Flux<Item> getCartItems() {
        return cartRepository.findByCountGreaterThan(0);
    }

    public Mono<Long> getTotal() {
        return cartRepository.getTotal();
    }

    public Flux<Item> increaseOrDecreaseCartItem(Long id, String action) {
        Mono<Void> operation;
        switch (action.toUpperCase()) {
            case "PLUS":
                operation = cartRepository.incrementCount(id).then();
                break;
            case "MINUS":
                operation = cartRepository.decrementCount(id).then();
                break;
            case "DELETE":
                operation = cartRepository.deleteFromCart(id).then();
                break;
            default:
                return Flux.error(new IllegalArgumentException("Неизвестное действие: " + action));
        }
        return operation.thenMany(getCartItems());
    }
}
