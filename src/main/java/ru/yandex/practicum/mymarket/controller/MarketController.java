package ru.yandex.practicum.mymarket.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.model.Order;
import ru.yandex.practicum.mymarket.model.Paging;
import ru.yandex.practicum.mymarket.service.CartService;
import ru.yandex.practicum.mymarket.service.FileService;
import ru.yandex.practicum.mymarket.service.MarketService;
import ru.yandex.practicum.mymarket.service.OrderService;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping
@Controller
public class MarketController {

    private final FileService fileService;
    private final MarketService marketService;
    private final CartService cartService;
    private final OrderService orderService;

    @GetMapping({"/", "/items"})
    public String getItems(@RequestParam(required = false) String search,
                           @RequestParam(required = false) String sort,
                           @RequestParam(required = false, defaultValue = "1") int pageNumber,
                           @RequestParam(required = false, defaultValue = "5") int pageSize,
                           Model model) {
        Paging paging = marketService.getPaging(search, pageNumber, pageSize);
        List<Item> items = marketService.getItems(search, sort, pageNumber, pageSize);
        model.addAttribute("items", items);
        model.addAttribute("paging", paging);
        return "items";
    }

    @PostMapping("/items")
    public String getIncreaseOrDecreaseItem(@RequestParam Long id,
                                            @RequestParam(required = false) String search,
                                            @RequestParam(required = false) String sort,
                                            @RequestParam(required = false, defaultValue = "1") int pageNumber,
                                            @RequestParam(required = false, defaultValue = "5") int pageSize,
                                            @RequestParam String action) {
        String searchRequest = "search=" + search;
        String sortRequest = "sort=" + sort;
        String pageNumberRequest = "pageNumber=" + pageNumber;
        String pageSizeRequest = "pageSize=" + pageSize;
        return "redirect:items?" + searchRequest + sortRequest + pageNumberRequest + pageSizeRequest;
    }

    @GetMapping("/items/{id}")
    public String getItem(@PathVariable Long id, Model model) {
        Item item = marketService.getItem(id);
        model.addAttribute("item", item);
        return "item";
    }

    @PostMapping("/items/{id}")
    public String getIncreaseOrDecreaseItem(@PathVariable Long id, @RequestParam String action, Model model) {
        Item item = marketService.getIncreaseOrDecreaseItem(id, action);
        model.addAttribute("item", item);
        return "item";
    }

    @GetMapping("/cart/items")
    public String getCartItems(Model model) {
        List<Item> items = cartService.getCartItems();
        int total = cartService.getTotal();
        model.addAttribute("items", items);
        model.addAttribute("total", total);
        return "cart";
    }

    @PostMapping("/cart/items")
    public String getIncreaseOrDecreaseCartItem(@RequestParam Long id, @RequestParam String action, Model model) {
        List<Item> items = cartService.getIncreaseOrDecreaseCartItem(id, action);
        int total = cartService.getTotal();
        model.addAttribute("items", items);
        model.addAttribute("total", total);
        return "cart";
    }

    @GetMapping("/orders")
    public String getOrders(Model model) {
        List<Order> orders = orderService.getOrders();
        model.addAttribute("orders", orders);
        return "orders";
    }

    @GetMapping("/orders/{id}")
    public String getNewOrder(@PathVariable Long id, @RequestParam(required = false) boolean newOrder, Model model) {
        Order order = orderService.getNewOrder(id);
        model.addAttribute("order", order);
        model.addAttribute("newOrder", newOrder);
        return "order";
    }

    @PostMapping("/buy")
    public String buy() {
        return "redirect:/orders/{id}?newOrder=true"; //TODO: пока непонятно где взять  id совершенного заказа
    }

    //TODO: возможно доделать параметры запроса
    @PutMapping("/{id}/image")
    public String uploadFile(@RequestParam("image") MultipartFile file, @PathVariable(name = "id") int id) {
        return fileService.upload(id, file);
    }
}
