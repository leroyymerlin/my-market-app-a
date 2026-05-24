package ru.yandex.practicum.mymarket.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.repository.CartRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {"spring.main.lazy-initialization=true"})
class CartServiceTest {

    @MockitoBean
    CartRepository cartRepository;

    @Autowired
    CartService cartService;

    @Test
    void getCartItems() {
        List<Item> expectedItems = List.of(
                new Item(1L, "Товар 1", "Описание 1", "/img/1.jpg", 100L, 2),
                new Item(2L, "Товар 2", "Описание 2", "/img/2.jpg", 200L, 1)
        );
        when(cartRepository.getCartItems()).thenReturn(expectedItems);

        List<Item> actualItems = cartService.getCartItems();

        assertEquals(expectedItems, actualItems);
        verify(cartRepository).getCartItems();
    }

    @Test
    void getTotal() {
        int expectedTotal = 1500;
        when(cartRepository.getTotal()).thenReturn(expectedTotal);

        int actualTotal = cartService.getTotal();

        assertEquals(expectedTotal, actualTotal);
        verify(cartRepository).getTotal();
    }

    @Test
    void getIncreaseOrDecreaseCartItem() {
        Long id = 1L;
        String action = "PLUS";
        List<Item> expectedItems = List.of(
                new Item(1L, "Товар 1", "Описание 1", "/img/1.jpg", 100L, 3),
                new Item(2L, "Товар 2", "Описание 2", "/img/2.jpg", 200L, 1)
        );
        when(cartRepository.getIncreaseOrDecreaseCartItem(id, action)).thenReturn(expectedItems);

        List<Item> actualItems = cartService.getIncreaseOrDecreaseCartItem(id, action);

        assertEquals(expectedItems, actualItems);
        verify(cartRepository).getIncreaseOrDecreaseCartItem(id, action);
    }
}