package ru.yandex.practicum.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.ya.domain.BalanceResponse;
import ru.ya.domain.ChargeRequest;
import ru.ya.domain.ChargeResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient
class PaymentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void charge() {
        ChargeRequest request = new ChargeRequest();
        request.setAmount(50_000L);

        webTestClient.post()
                .uri("/payment/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ChargeResponse.class)
                .value(res -> assertThat(res.getSuccess()).isTrue());
    }

    @Test
    void getBalance() {
        webTestClient.get()
                .uri("/payment/balance")
                .exchange()
                .expectStatus().isOk()
                .expectBody(BalanceResponse.class);
    }
}