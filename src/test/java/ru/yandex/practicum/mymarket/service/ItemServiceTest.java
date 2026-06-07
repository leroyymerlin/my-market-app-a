package ru.yandex.practicum.mymarket.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.model.Paging;
import ru.yandex.practicum.mymarket.repository.ItemRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest(properties = {"spring.main.lazy-initialization=true"})
@Transactional
class ItemServiceTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemService itemService;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();

        Item item1 = new Item();
        item1.setId(1L);
        item1.setTitle("телефон");
        item1.setDescription("сенсорный");
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
    void printRepositoryClass() {
        System.out.println(itemRepository.getClass().getName());
    }

    @Test
    void getItems() {
        List<Item> items = itemService.getItems(null, null, 1, 2);
        assertThat(items).hasSize(2);
        assertThat(items.get(0).getId()).isEqualTo(1L);
        assertThat(items.get(1).getId()).isEqualTo(2L);
    }

    @Test
    void getItems_withFilter() {
        List<Item> items = itemService.getItems("телефон", null, 1, 10);
        assertThat(items).hasSize(2);
    }

    @Test
    void getItems_withSortAlpha() {
        List<Item> items = itemService.getItems(null, "ALPHA", 1, 10);
        assertThat(items).extracting(Item::getTitle).containsExactly("наушники", "телефон", "чехол");
    }

    @Test
    void getPaging() {
        Paging paging = itemService.getPaging(null, 2, 2, null);
        assertThat(paging.getPageNumber()).isEqualTo(2);
        assertThat(paging.getPageSize()).isEqualTo(2);
        assertThat(paging.isHasPrev()).isTrue();
        assertThat(paging.isHasNext()).isFalse();
    }

    @Test
    void getItem() {
        Item item = itemService.getItem(1L);
        assertThat(item.getTitle()).isEqualTo("телефон");
    }

    @Test
    void getIncreaseOrDecreaseItem() {
        Item updated = itemService.getIncreaseOrDecreaseItem(1L, "PLUS");
        assertThat(updated.getCount()).isEqualTo(3);
    }
}