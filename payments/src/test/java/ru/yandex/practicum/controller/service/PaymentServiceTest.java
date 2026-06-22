package ru.yandex.practicum.controller.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import ru.ya.domain.ChargeRequest;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentServiceTest {

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService();
    }

    @Test
    void getBalance() {
        StepVerifier.create(paymentService.getBalance())
                .assertNext(res -> assertThat(res.getBalance()).isEqualTo(200_000L))
                .verifyComplete();
    }

    @Test
    void charge() {
        long amount = 300_000L;
        ChargeRequest request = new ChargeRequest();
        request.setAmount(amount);

        StepVerifier.create(paymentService.charge(request))
                .assertNext(response -> {
                    assertThat(response.getSuccess()).isFalse();
                    assertThat(response.getMessage()).isEqualTo("Недостаточно средств");
                    assertThat(response.getNewBalance()).isEqualTo(200_000L);
                })
                .verifyComplete();

        StepVerifier.create(paymentService.getBalance())
                .assertNext(res -> assertThat(res.getBalance()).isEqualTo(200_000L))
                .verifyComplete();
    }

    @Test
    void charge_multipleCharges() {
        long amount1 = 30_000L;
        long amount2 = 70_000L;
        ChargeRequest request1 = new ChargeRequest();
        request1.setAmount(amount1);
        ChargeRequest request2 = new ChargeRequest();
        request2.setAmount(amount2);

        StepVerifier.create(paymentService.charge(request1))
                .assertNext(res -> assertThat(res.getNewBalance()).isEqualTo(170_000L))
                .verifyComplete();

        StepVerifier.create(paymentService.charge(request2))
                .assertNext(res -> assertThat(res.getNewBalance()).isEqualTo(100_000L))
                .verifyComplete();

        StepVerifier.create(paymentService.getBalance())
                .assertNext(res -> assertThat(res.getBalance()).isEqualTo(100_000L))
                .verifyComplete();
    }
}