package ru.yandex.practicum.mymarket.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.yandex.practicum.mymarket.model.Order;
import ru.yandex.practicum.mymarket.repository.OrderRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {"spring.main.lazy-initialization=true"})
class OrderServiceTest {

    @MockitoBean
    OrderRepository orderRepository;

    @Autowired
    OrderService orderService;

    @Test
    void getOrders() {
        List<Order> expectedOrders = List.of(new Order(), new Order());
        when(orderRepository.getOrders()).thenReturn(expectedOrders);

        List<Order> actualOrders = orderService.getOrders();

        assertEquals(expectedOrders, actualOrders);
        verify(orderRepository).getOrders();
    }

    @Test
    void getNewOrder() {
        Long id = 1L;
        Order expectedOrder = new Order();
        expectedOrder.setId(id);
        when(orderRepository.getNewOrder(id)).thenReturn(expectedOrder);

        Order actualOrder = orderService.getNewOrder(id);

        assertEquals(expectedOrder, actualOrder);
        verify(orderRepository).getNewOrder(id);
    }
}