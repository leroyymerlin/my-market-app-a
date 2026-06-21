package ru.yandex.practicum.mymarket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.model.Paging;
import ru.yandex.practicum.mymarket.repository.ItemRepository;

@RequiredArgsConstructor
@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public Flux<Item> getItems(String search, String sort, int pageNumber, int pageSize) {
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

}
