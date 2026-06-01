package ru.yandex.practicum.mymarket.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mymarket.model.Item;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@Transactional
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    EntityManager entityManager;

    @BeforeEach
    void setUp() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setTitle("Товар 1");
        item1.setPrice(100L);
        item1.setCount(2);
        cartRepository.save(item1);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setTitle("Товар 2");
        item2.setPrice(200L);
        item2.setCount(0);
        cartRepository.save(item2);
    }

    @Test
    void findByCountGreaterThan() {
        List<Item> items = cartRepository.findByCountGreaterThan(0);
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void getTotal() {
        Long total = cartRepository.getTotal();
        assertThat(total).isEqualTo(200L);
    }

    @Test
    void incrementCount() {
        cartRepository.incrementCount(1L);
        entityManager.clear();
        Item updated = cartRepository.findById(1L).orElseThrow();
        assertThat(updated.getCount()).isEqualTo(3);
    }

    @Test
    void decrementCount() {
        cartRepository.decrementCount(1L);
        entityManager.clear();
        Item updated = cartRepository.findById(1L).orElseThrow();
        assertThat(updated.getCount()).isEqualTo(1);
    }

    @Test
    void deleteFromCart() {
        cartRepository.deleteFromCart(1L);
        entityManager.clear();
        Item updated = cartRepository.findById(1L).orElseThrow();
        assertThat(updated.getCount()).isZero();
    }
}