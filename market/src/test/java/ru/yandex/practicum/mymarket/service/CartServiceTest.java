package ru.yandex.practicum.mymarket.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.repository.CartRepository;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {"spring.main.lazy-initialization=true"})
class CartServiceTest {

    @MockitoBean
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    @BeforeEach
    void setUp() {
        when(cartRepository.findByCountGreaterThan(0))
                .thenReturn(Flux.just(
                        new Item(1L, "телефон", "кнопочный", "img", 30000L, 2),
                        new Item(2L, "наушники", "беспроводные", "img", 5000L, 0)
                ));
        when(cartRepository.getTotal()).thenReturn(Mono.just(65000L));
        when(cartRepository.incrementCount(1L)).thenReturn(Mono.empty());
        when(cartRepository.decrementCount(1L)).thenReturn(Mono.empty());
        when(cartRepository.deleteFromCart(1L)).thenReturn(Mono.empty());
    }

    @Test
    void getCartItems() {
        cartService.getCartItems()
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void getTotal() {
        cartService.getTotal()
                .as(StepVerifier::create)
                .expectNext(65000L)
                .verifyComplete();
    }

    @Test
    void increaseCartItem_plus() {
        when(cartRepository.findByCountGreaterThan(0))
                .thenReturn(Flux.just(
                        new Item(1L, "телефон", "кнопочный", "img",30000L, 3),
                        new Item(2L, "наушники", "беспроводные", "img", 5000L, 1)
                ));
        when(cartRepository.getTotal()).thenReturn(Mono.just(95000L));

        cartService.increaseOrDecreaseCartItem(1L, "PLUS")
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void increaseCartItem_minus() {
        when(cartRepository.findByCountGreaterThan(0))
                .thenReturn(Flux.just(
                        new Item(1L, "телефон", "кнопочный", "img", 30000L, 1),
                        new Item(2L, "наушники", "беспроводные", "img", 5000L, 1)
                ));
        when(cartRepository.getTotal()).thenReturn(Mono.just(35000L));

        cartService.increaseOrDecreaseCartItem(1L, "MINUS")
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void increaseCartItem_delete() {
        when(cartRepository.findByCountGreaterThan(0))
                .thenReturn(Flux.just(new Item(2L, "наушники", "беспроводные", "img", 5000L, 1)));
        when(cartRepository.getTotal()).thenReturn(Mono.just(5000L));

        cartService.increaseOrDecreaseCartItem(1L, "DELETE")
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }
}