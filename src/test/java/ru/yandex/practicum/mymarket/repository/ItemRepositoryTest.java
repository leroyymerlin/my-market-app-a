package ru.yandex.practicum.mymarket.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;
import ru.yandex.practicum.mymarket.model.Item;

@DataR2dbcTest
@ActiveProfiles("test")
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    private Long item1Id;
    private Long item2Id;
    private Long item3Id;

    @BeforeEach
    void setUp() {
        Item item1 = new Item();
        item1.setTitle("телефон");
        item1.setDescription("сенсорный телефон");
        item1.setPrice(30000L);
        item1.setCount(2);

        Item item2 = new Item();
        item2.setTitle("наушники");
        item2.setDescription("беспроводные наушники");
        item2.setPrice(5000L);
        item2.setCount(0);

        Item item3 = new Item();
        item3.setTitle("чехол");
        item3.setDescription("чехол Для телефона");
        item3.setPrice(1000L);
        item3.setCount(5);

        itemRepository.deleteAll()
                .then(itemRepository.save(item1))
                .doOnNext(saved -> item1Id = saved.getId())
                .then(itemRepository.save(item2))
                .doOnNext(saved -> item2Id = saved.getId())
                .then(itemRepository.save(item3))
                .doOnNext(saved -> item3Id = saved.getId())
                .block();
    }

    @Test
    void incrementCount() {
        itemRepository.incrementCount(item1Id)
                .then(itemRepository.findById(item1Id))
                .as(StepVerifier::create)
                .expectNextMatches(item -> item.getCount() == 3)
                .verifyComplete();
    }

    @Test
    void decrementCount() {
        itemRepository.findByTitleContainingOrDescriptionContaining("телефон", "телефон", Sort.by("id"))
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void findByTitleContainingOrDescriptionContaining() {
        itemRepository.findByTitleContainingOrDescriptionContaining("телефон", "телефон", Sort.by("id"))
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void findByCountGreaterThan() {
        itemRepository.findByCountGreaterThan(0)
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }
}