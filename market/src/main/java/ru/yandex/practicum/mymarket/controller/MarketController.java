package ru.yandex.practicum.mymarket.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ya.client.api.DefaultApi;
import ru.ya.client.domain.BalanceResponse;
import ru.ya.client.domain.ChargeRequest;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.model.Order;
import ru.yandex.practicum.mymarket.model.Paging;
import ru.yandex.practicum.mymarket.service.CartService;
import ru.yandex.practicum.mymarket.service.FileService;
import ru.yandex.practicum.mymarket.service.ItemService;
import ru.yandex.practicum.mymarket.service.OrderService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping
@Controller
public class MarketController {

    private final FileService fileService;
    private final ItemService marketService;
    private final CartService cartService;
    private final OrderService orderService;

    private final DefaultApi paymentApi;

    @GetMapping({"/", "/items"})
    public Mono<String> getItems(@RequestParam(required = false) String search,
                                 @RequestParam(required = false) String sort,
                                 @RequestParam(required = false, defaultValue = "1") int pageNumber,
                                 @RequestParam(required = false, defaultValue = "5") int pageSize,
                                 Model model) {
        Mono<Paging> paging = marketService.getPaging(search, pageNumber, pageSize, sort);
        Flux<Item> items = marketService.getItems(search, sort, pageNumber, pageSize);
        model.addAttribute("items", items);
        model.addAttribute("paging", paging);
        return Mono.just("items");
    }

    @PostMapping("/items")
    public Mono<String> getIncreaseOrDecreaseItem(@RequestParam Long id,
                                            @RequestParam(required = false) String search,
                                            @RequestParam(required = false) String sort,
                                            @RequestParam(required = false, defaultValue = "1") int pageNumber,
                                            @RequestParam(required = false, defaultValue = "5") int pageSize,
                                            @RequestParam String action) {
        String searchRequest = "search=" + (search != null ? search : "");
        String sortRequest = "&sort=" + (sort != null ? sort : "");
        String pageNumberRequest = "&pageNumber=" + pageNumber;
        String pageSizeRequest = "&pageSize=" + pageSize;
        String redirectUrl = "redirect:/items?" + searchRequest + sortRequest + pageNumberRequest + pageSizeRequest;
        return Mono.just(redirectUrl);
    }

    @GetMapping("/items/{id}")
    public Mono<String> getItem(@PathVariable Long id, Model model) {
        Mono<Item> item = marketService.getItem(id);
        model.addAttribute("item", item);
        return Mono.just("item");
    }

    @PostMapping("/items/{id}")
    public Mono<String> getIncreaseOrDecreaseItem(@PathVariable Long id, @RequestParam String action, Model model) {
        Mono<Item> item = marketService.getIncreaseOrDecreaseItem(id, action);
        model.addAttribute("item", item);
        return Mono.just("item");
    }

    @GetMapping("/cart/items")
    public Mono<String> getCartItems(Model model) {
        Flux<Item> items = cartService.getCartItems();
        Mono<Long> total = cartService.getTotal();

        Mono<BalanceResponse> balance = paymentApi.getBalance()
                .defaultIfEmpty(new BalanceResponse().balance(0L));

        return Mono.zip(items.collectList(), total, balance)
                .map(tuple -> {
                    List<Item> itemList = tuple.getT1();
                    Long totalAmount = tuple.getT2();
                    BalanceResponse balanceResp = tuple.getT3();
                    long balanceAmount = balanceResp.getBalance();

                    boolean canBuy = totalAmount > 0 && balanceAmount >= totalAmount;
                    model.addAttribute("items", itemList);
                    model.addAttribute("total", totalAmount);
                    model.addAttribute("canBuy", canBuy);
                    return "cart";
                });
    }

    @PostMapping("/cart/items")
    public Mono<String> getIncreaseOrDecreaseCartItem(@RequestParam Long id, @RequestParam String action, Model model) {
        return cartService.increaseOrDecreaseCartItem(id, action)
                .then(Mono.zip(
                        cartService.getCartItems().collectList(),
                        cartService.getTotal()
                ))
                .doOnNext(tuple -> {
                    model.addAttribute("items", tuple.getT1());
                    model.addAttribute("total", tuple.getT2());
                })
                .thenReturn("cart");
    }

    @GetMapping("/orders")
    public Mono<String> getOrders(Model model) {
        Flux<Order> orders = orderService.getOrders();
        model.addAttribute("orders", orders);
        return Mono.just("orders");
    }

    @GetMapping("/orders/{id}")
    public Mono<String> getNewOrder(@PathVariable Long id, @RequestParam(required = false) boolean newOrder, Model model) {
        Mono<Order> order = orderService.getNewOrder(id);
        model.addAttribute("order", order);
        model.addAttribute("newOrder", newOrder);
        return Mono.just("order");
    }

    @PostMapping("/buy")
    public Mono<String> buy() {
        return cartService.getTotal()
                .flatMap(total -> {
                    if (total == 0) {
                        return Mono.just("redirect:/cart/items?error=Корзина пуста");
                    }
                    return paymentApi.charge(new ChargeRequest().amount(total))
                            .flatMap(chargeResponse -> {
                                if (chargeResponse.getSuccess()) {
                                    return orderService.createOrderFromCart()
                                            .map(orderId -> "redirect:/orders/" + orderId + "?newOrder=true");
                                } else {
                                    String message = chargeResponse.getMessage() != null
                                            ? chargeResponse.getMessage()
                                            : "Ошибка оплаты";
                                    return Mono.just("redirect:/cart/items?error=" + message);
                                }
                            });
                })
                .onErrorResume(e -> Mono.just("redirect:/cart/items?error=Сервис платежей недоступен"));
    }

    @PutMapping("/{id}/image")
    public Mono<Void> uploadFile(@RequestParam("image") byte[] file, @PathVariable(name = "id") Long id) {
        return fileService.upload(id, file)
                .doOnError(e -> log.error("Ошибка загрузки файла для id={}", id, e))
                .then();
    }
}
