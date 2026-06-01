package ru.yandex.practicum.mymarket.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mymarket.model.Item;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@Transactional
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    EntityManager entityManager;

    @BeforeEach
    void setUp() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setTitle("телефон");
        item1.setDescription("кнопочный");
        item1.setPrice(30000L);
        item1.setCount(2);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setTitle("наушники");
        item2.setDescription("беспроводные наушники");
        item2.setPrice(5000L);
        item2.setCount(0);
        itemRepository.save(item2);

        Item item3 = new Item();
        item3.setId(3L);
        item3.setTitle("чехол");
        item3.setDescription("чехол для телефона");
        item3.setPrice(1000L);
        item3.setCount(5);
        itemRepository.save(item3);

    }

    @Test
    void incrementCount() {
        itemRepository.incrementCount(1L);
        entityManager.clear();
        Item item = itemRepository.findById(1L).orElseThrow();
        assertThat(item.getCount()).isEqualTo(3);
    }

    @Test
    void decrementCount() {
        itemRepository.decrementCount(1L);
        entityManager.clear();
        Item item = itemRepository.findById(1L).orElseThrow();
        assertThat(item.getCount()).isEqualTo(1);
    }

    @Test
    void findByTitleContainingOrDescriptionContaining() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> page = itemRepository.findByTitleContainingOrDescriptionContaining("телефон", "телефон", pageable);
        assertThat(page.getContent()).hasSize(2);
    }

    @Test
    void findByCountGreaterThan() {
        List<Item> items = itemRepository.findByCountGreaterThan(0);
        assertThat(items).hasSize(2);
        assertThat(items).extracting(Item::getId).containsExactlyInAnyOrder(1L, 3L);
    }
}