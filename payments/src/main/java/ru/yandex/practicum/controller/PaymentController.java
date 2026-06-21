package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<ChargeResponse> charge(ChargeRequest chargeRequest) {
        ChargeResponse response = paymentService.charge(chargeRequest);
        return ResponseEntity.ok().body(response);
    }

    @Override
    public ResponseEntity<BalanceResponse> getBalance() {
        return ResponseEntity.ok().body(new BalanceResponse().balance(200000L));
    }
}
