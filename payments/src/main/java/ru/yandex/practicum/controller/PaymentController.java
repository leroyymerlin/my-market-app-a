package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.ya.api.DefaultApi;
import ru.ya.domain.BalanceResponse;
import ru.ya.domain.ChargeRequest;
import ru.ya.domain.ChargeResponse;
import ru.yandex.practicum.controller.service.PaymentService;

@RequiredArgsConstructor
@RestController
public class PaymentController implements DefaultApi {

    private final PaymentService paymentService;

    @Override
    public Mono<ResponseEntity<ChargeResponse>> charge(Mono<ChargeRequest> chargeRequest, ServerWebExchange exchange) {
        return chargeRequest
                .flatMap(paymentService::charge)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @Override
    public Mono<ResponseEntity<BalanceResponse>> getBalance(ServerWebExchange exchange) {
        return paymentService.getBalance()
                .map(ResponseEntity::ok);
    }
}
