package ru.yandex.practicum.mymarket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.model.Paging;
import ru.yandex.practicum.mymarket.repository.ItemRepository;

import java.time.Duration;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    @Value("${app.cache.items.ttl:300}")
    private long ttlSeconds;

    private static final String CACHE_KEY_PREFIX = "items:";

    public Flux<Item> getItems(String search, String sort, int pageNumber, int pageSize) {
        String key = buildCacheKey(search, sort, pageNumber, pageSize);
        return redisTemplate.opsForValue().get(key)
                .cast(List.class)
                .flatMapMany(items -> {
                    if (items != null && !items.isEmpty()) {
                        return Flux.fromIterable(items);
                    } else {
                        return fetchItemsFromDb(search, sort, pageNumber, pageSize)
                                .collectList()
                                .flatMap(list -> {
                                    return redisTemplate.opsForValue()
                                            .set(key, list, Duration.ofSeconds(ttlSeconds))
                                            .thenReturn(list);
                                })
                                .flatMapMany(Flux::fromIterable);
                    }
                })
                .switchIfEmpty(
                        fetchItemsFromDb(search, sort, pageNumber, pageSize)
                                .collectList()
                                .flatMap(list -> {
                                    return redisTemplate.opsForValue()
                                            .set(key, list, Duration.ofSeconds(ttlSeconds))
                                            .thenReturn(list);
                                })
                                .flatMapMany(Flux::fromIterable)
                );
    }

    public Mono<Paging> getPaging(String search, int pageNumber, int pageSize, String sortType) {
        Mono<Long> totalCount;

        if (search != null && !search.isBlank()) {
            totalCount = itemRepository.countByTitleContainingOrDescriptionContaining(search, search);
        } else {
            totalCount = itemRepository.count();
        }

        return totalCount.map(total -> {
            Paging paging = new Paging();
            paging.setPageNumber(pageNumber);
            paging.setPageSize(pageSize);
            paging.setHasPrev(pageNumber > 1);
            paging.setHasNext((long) pageNumber * pageSize < total);
            return paging;
        });
    }

    private Sort buildSort(String sortType) {
        if (sortType == null) return Sort.by("id").ascending();
        return switch (sortType) {
            case "ALPHA" -> Sort.by("title").ascending();
            case "PRICE" -> Sort.by("price").ascending();
            default -> Sort.by("id").ascending();
        };
    }

    @Transactional(readOnly = true)
    public Mono<Item> getItem(Long id) {
        return itemRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Не удалось найти запись")));
    }

    public Mono<Item> getIncreaseOrDecreaseItem(Long id, String action) {
        return ("PLUS".equalsIgnoreCase(action)
                ? itemRepository.incrementCount(id).then()
                : itemRepository.decrementCount(id).then())
                .then(getItem(id));
    }

    public Mono<Void> clearItemsCache() {
        return redisTemplate.keys(CACHE_KEY_PREFIX + "*")
                .flatMap(redisTemplate::delete)
                .then();
    }

    private String buildCacheKey(String search, String sort, int pageNumber, int pageSize) {
        return CACHE_KEY_PREFIX + "search=" + (search != null ? search : "")
                + "&sort=" + (sort != null ? sort : "")
                + "&page=" + pageNumber
                + "&size=" + pageSize;
    }

    private Flux<Item> fetchItemsFromDb(String search, String sort, int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;
        Sort sortObj = buildSort(sort);
        if (search != null && !search.isBlank()) {
            return itemRepository.findByTitleContainingOrDescriptionContaining(search, search, sortObj)
                    .skip(offset)
                    .take(pageSize);
        } else {
            return itemRepository.findAll(sortObj)
                    .skip(offset)
                    .take(pageSize);
        }
    }
}
