package ru.yandex.practicum.mymarket.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ru.yandex.practicum.mymarket.model.Item;

@DataR2dbcTest
@ActiveProfiles("test")
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    private Long item1Id;
    private Long item2Id;

    @BeforeEach
    void setUp() {
        Item item1 = new Item();
        item1.setTitle("Товар 1");
        item1.setPrice(100L);
        item1.setCount(2);

        Item item2 = new Item();
        item2.setTitle("Товар 2");
        item2.setPrice(200L);
        item2.setCount(0);

        cartRepository.deleteAll()
                .thenMany(cartRepository.saveAll(Flux.just(item1, item2)))
                .collectList()
                .doOnNext(items -> {
                    item1Id = items.get(0).getId();
                    item2Id = items.get(1).getId();
                })
                .block();
    }

    @Test
    void findByCountGreaterThan() {
        cartRepository.findByCountGreaterThan(0)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getTotal() {
        cartRepository.getTotal()
                .as(StepVerifier::create)
                .expectNext(200L)
                .verifyComplete();
    }

    @Test
    void incrementCount() {
        cartRepository.incrementCount(item1Id)
                .then(cartRepository.findById(item1Id))
                .as(StepVerifier::create)
                .expectNextMatches(item -> item.getCount() == 3)
                .verifyComplete();
    }

    @Test
    void decrementCount() {
        cartRepository.decrementCount(item1Id)
                .then(cartRepository.findById(item1Id))
                .as(StepVerifier::create)
                .expectNextMatches(item -> item.getCount() == 1)
                .verifyComplete();
    }

    @Test
    void deleteFromCart() {
        cartRepository.deleteFromCart(item1Id)
                .then(cartRepository.findById(item1Id))
                .as(StepVerifier::create)
                .expectNextMatches(item -> item.getCount() == 0)
                .verifyComplete();
    }
}