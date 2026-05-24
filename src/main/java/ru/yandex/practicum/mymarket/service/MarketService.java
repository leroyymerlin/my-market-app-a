package ru.yandex.practicum.mymarket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.model.Paging;
import ru.yandex.practicum.mymarket.repository.MarketRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MarketService {

    private final MarketRepository marketRepository;

    public List<Item> getItems(String search, String sort, int pageNumber, int pageSize) {
        return marketRepository.getItems(search, sort, pageNumber, pageSize);
    }

    public Paging getPaging(String search, int pageNumber, int pageSize) {

        return marketRepository.getPaging(search, pageNumber, pageSize);
    }

    public Item getItem(Long id) {
        return marketRepository.getItem(id);
    }

    public Item getIncreaseOrDecreaseItem(Long id, String action) {
        return marketRepository.getIncreaseOrDecreaseItem(id, action);
    }

}
