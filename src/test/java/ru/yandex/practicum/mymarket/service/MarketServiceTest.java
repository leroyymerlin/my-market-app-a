package ru.yandex.practicum.mymarket.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.model.Paging;
import ru.yandex.practicum.mymarket.repository.MarketRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {"spring.main.lazy-initialization=true"})
class MarketServiceTest {

    @MockitoBean
    MarketRepository marketRepository;

    @Autowired
    MarketService marketService;

    @Test
    void getItems() {
        String search = "test";
        String sort = "ALPHA";
        int pageNumber = 1;
        int pageSize = 5;
        List<Item> expectedItems = List.of(new Item(), new Item());
        when(marketRepository.getItems(search, sort, pageNumber, pageSize)).thenReturn(expectedItems);

        List<Item> actualItems = marketService.getItems(search, sort, pageNumber, pageSize);

        assertEquals(expectedItems, actualItems);
        verify(marketRepository).getItems(search, sort, pageNumber, pageSize);
    }

    @Test
    void getPaging() {
        String search = "test";
        int pageNumber = 2;
        int pageSize = 10;
        Paging expectedPaging = new Paging();
        expectedPaging.setPageNumber(pageNumber);
        expectedPaging.setPageSize(pageSize);
        when(marketRepository.getPaging(search, pageNumber, pageSize)).thenReturn(expectedPaging);

        Paging actualPaging = marketService.getPaging(search, pageNumber, pageSize);

        assertEquals(expectedPaging, actualPaging);
        verify(marketRepository).getPaging(search, pageNumber, pageSize);
    }

    @Test
    void getItem() {
        Long id = 1L;
        Item expectedItem = new Item();
        expectedItem.setId(id);
        when(marketRepository.getItem(id)).thenReturn(expectedItem);

        Item actualItem = marketService.getItem(id);

        assertEquals(expectedItem, actualItem);
        verify(marketRepository).getItem(id);
    }

    @Test
    void getIncreaseOrDecreaseItem() {
        Long id = 1L;
        String action = "PLUS";
        Item expectedItem = new Item();
        expectedItem.setId(id);
        expectedItem.setCount(3);
        when(marketRepository.getIncreaseOrDecreaseItem(id, action)).thenReturn(expectedItem);

        Item actualItem = marketService.getIncreaseOrDecreaseItem(id, action);

        assertEquals(expectedItem, actualItem);
        verify(marketRepository).getIncreaseOrDecreaseItem(id, action);
    }
}