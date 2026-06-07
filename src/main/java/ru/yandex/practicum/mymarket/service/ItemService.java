package ru.yandex.practicum.mymarket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.model.Paging;
import ru.yandex.practicum.mymarket.repository.ItemRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public List<Item> getItems(String search, String sort, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, buildSort(sort));
        Page<Item> page;
        if (search != null && !search.isBlank()) {
            page = itemRepository.findByTitleContainingOrDescriptionContaining(search, search, pageable);
        } else {
            page = itemRepository.findAll(pageable);
        }
        return page.getContent();
    }

    public Paging getPaging(String search, int pageNumber, int pageSize, String sortType) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, buildSort(sortType));

        Page<Item> page;
        if (search != null && !search.isBlank()) {
            page = itemRepository.findByTitleContainingOrDescriptionContaining(search, search, pageable);
        } else {
            page = itemRepository.findAll(pageable);
        }

        Paging paging = new Paging();
        paging.setPageNumber(pageNumber);
        paging.setPageSize(pageSize);
        paging.setHasPrev(page.hasPrevious());
        paging.setHasNext(page.hasNext());
        return paging;
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
    public Item getItem(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Не удалось найти запись"));
    }

    public Item getIncreaseOrDecreaseItem(Long id, String action) {
        if ("PLUS".equalsIgnoreCase(action)) {
            itemRepository.incrementCount(id);
        } else {
            itemRepository.decrementCount(id);
        }
        return getItem(id);
    }

}
