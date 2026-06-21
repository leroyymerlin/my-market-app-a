package ru.yandex.practicum.mymarket.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.model.Paging;
import ru.yandex.practicum.mymarket.service.CartService;
import ru.yandex.practicum.mymarket.service.FileService;
import ru.yandex.practicum.mymarket.service.ItemService;
import ru.yandex.practicum.mymarket.service.OrderService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebFluxTest(MarketController.class)
class MarketControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private FileService fileService;
    @MockitoBean
    private ItemService marketService;
    @MockitoBean
    private CartService cartService;
    @MockitoBean
    private OrderService orderService;

    @Test
    void getItems() {
        when(marketService.getItems(any(), any(), anyInt(), anyInt()))
                .thenReturn(Flux.just(new Item(1L, "Телефон", "", "img", 30000L, 2)));
        when(marketService.getPaging(any(), anyInt(), anyInt(), any()))
                .thenReturn(Mono.just(new Paging(false, true, 1, 5)));

        webTestClient.get()
                .uri("/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .consumeWith(response -> {
                    String body = response.getResponseBody();
                    Assertions.assertNotEquals(null, body);
                });
    }

    @Test
    void getIncreaseOrDecreaseItem() {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/items")
                        .queryParam("id", 1)
                        .queryParam("action", "PLUS")
                        .queryParam("pageNumber", 2)
                        .build())
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/items?search=&sort=&pageNumber=2&pageSize=5");
    }

    @Test
    void getItem() {
        when(marketService.getItem(1L)).thenReturn(Mono.just(new Item(1L, "", "img","Телефон", 30000L, 2)));

        webTestClient.get()
                .uri("/items/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(res -> res.getResponseBody().contains("Телефон"));
    }

    @Test
    void buy() {
        when(orderService.createOrderFromCart()).thenReturn(Mono.just(123L));

        webTestClient.post()
                .uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/orders/123?newOrder=true");
    }
}