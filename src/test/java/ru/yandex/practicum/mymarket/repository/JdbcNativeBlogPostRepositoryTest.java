package ru.yandex.practicum.mymarket.repository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:test-application.properties")
class JdbcNativeBlogPostRepositoryTest {

    @Test
    void getItems() {
    }

    @Test
    void getPaging() {
    }

    @Test
    void getItem() {
    }

    @Test
    void getIncreaseOrDecreaseItem() {
    }

    @Test
    void getCartItems() {
    }

    @Test
    void getTotal() {
    }

    @Test
    void getIncreaseOrDecreaseCartItem() {
    }

    @Test
    void addImage() {
    }

    @Test
    void getOrders() {
    }

    @Test
    void getNewOrder() {
    }
}