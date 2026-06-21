package ru.yandex.practicum.mymarket.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.repository.ItemRepository;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {"spring.main.lazy-initialization=true", "app.cache.items.ttl=300"})
class ItemServiceTest {

    @MockitoBean
    ItemRepository itemRepository;

    @MockitoBean
    ReactiveRedisTemplate<String, Object> redisTemplate;

    @Autowired
    ItemService itemService;

    @BeforeEach
    void setUp() {

        ReactiveValueOperations<String, Object> valueOperations = mock(ReactiveValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(any())).thenReturn(Mono.empty());
        when(valueOperations.set(any(), any(), any())).thenReturn(Mono.empty());

        when(redisTemplate.keys(any())).thenReturn(Flux.empty());
        when(redisTemplate.delete((String) any())).thenReturn(Mono.empty());

        Item item1 = new Item();
        item1.setId(1L);
        item1.setTitle("телефон");
        item1.setDescription("сенсорный");
        item1.setPrice(30000L);
        item1.setCount(2);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setTitle("наушники");
        item2.setDescription("беспроводные наушники");
        item2.setPrice(5000L);
        item2.setCount(0);

        Item item3 = new Item();
        item3.setId(3L);
        item3.setTitle("чехол");
        item3.setDescription("чехол для телефона");
        item3.setPrice(1000L);
        item3.setCount(5);

        when(itemRepository.findAll(any(Sort.class))).thenReturn(Flux.just(item2, item1, item3));
        when(itemRepository.findByTitleContainingOrDescriptionContaining(eq("телефон"), eq("телефон"), any(Sort.class)))
                .thenReturn(Flux.just(item1, item3));
        when(itemRepository.count()).thenReturn(Mono.just(3L));
        when(itemRepository.countByTitleContainingOrDescriptionContaining(eq("телефон"), eq("телефон")))
                .thenReturn(Mono.just(2L));
        when(itemRepository.findById(1L)).thenReturn(Mono.just(item1));
        when(itemRepository.incrementCount(1L)).thenReturn(Mono.empty());
        when(itemRepository.decrementCount(1L)).thenReturn(Mono.empty());
    }

    @Test
    void getItems() {
        itemService.getItems(null, null, 1, 2)
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void getItems_withFilter() {
        itemService.getItems("телефон", null, 1, 10)
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void getItems_withSortAlpha() {
        itemService.getItems(null, "ALPHA", 1, 10)
                .as(StepVerifier::create)
                .expectNextMatches(i -> i.getTitle().equals("наушники"))
                .expectNextMatches(i -> i.getTitle().equals("телефон"))
                .expectNextMatches(i -> i.getTitle().equals("чехол"))
                .verifyComplete();
    }

    @Test
    void getPaging() {
        itemService.getPaging(null, 2, 2, null)
                .as(StepVerifier::create)
                .assertNext(paging -> {
                    assert paging.getPageNumber() == 2;
                    assert paging.getPageSize() == 2;
                    assert paging.isHasPrev();
                    assert !paging.isHasNext();
                })
                .verifyComplete();
    }

    @Test
    void getItem() {
        itemService.getItem(1L)
                .as(StepVerifier::create)
                .expectNextMatches(item -> item.getTitle().equals("телефон"))
                .verifyComplete();
    }

    @Test
    void getIncreaseOrDecreaseItem() {
        Item updatedItem = new Item();
        updatedItem.setId(1L);
        updatedItem.setTitle("телефон");
        updatedItem.setDescription("сенсорный");
        updatedItem.setPrice(30000L);
        updatedItem.setCount(3);

        when(itemRepository.findById(1L)).thenReturn(Mono.just(updatedItem));

        itemService.getIncreaseOrDecreaseItem(1L, "PLUS")
                .as(StepVerifier::create)
                .expectNextMatches(item -> item.getCount() == 3)
                .verifyComplete();
    }

    @Test
    void getItems_cacheEmpty() {
        when(redisTemplate.opsForValue().get(any())).thenReturn(Mono.empty());
        when(redisTemplate.opsForValue().set(any(), any(), any())).thenReturn(Mono.empty());
        StepVerifier.create(itemService.getItems(null, null, 1, 2))
                .expectNextCount(2)
                .verifyComplete();
        verify(redisTemplate.opsForValue(), times(1)).set(any(), any(), any());
    }

    @Test
    void getItems_cacheHit() {
        List<Item> cachedItems = Arrays.asList(new Item(1L, "телефон", "сенсорный", "", 30000L, 2),
                new Item(2L, "наушники", "проводные", "", 5000L, 0));
        ReactiveValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        lenient().when(valueOperations.get(anyString())).thenReturn(Mono.just((Object) cachedItems));

        StepVerifier.create(itemService.getItems(null, null, 1, 2))
                .expectNextCount(2)
                .verifyComplete();

        verify(valueOperations, times(1)).get(anyString());
        verify(itemRepository, never()).findByTitleContainingOrDescriptionContaining(any(), any(), any());
        verify(valueOperations, never()).set(anyString(), any(), any());
    }
}