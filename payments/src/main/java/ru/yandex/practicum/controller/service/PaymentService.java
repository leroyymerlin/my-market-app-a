package ru.yandex.practicum.controller.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.ya.domain.BalanceResponse;
import ru.ya.domain.ChargeRequest;
import ru.ya.domain.ChargeResponse;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class PaymentService {

    private final AtomicLong balance = new AtomicLong(200_000L);

    public Mono<BalanceResponse> getBalance() {
        return Mono.just(new BalanceResponse().balance(balance.get()));
    }

    public Mono<ChargeResponse> charge(ChargeRequest request) {
        long amount = request.getAmount();
        return Mono.defer(() -> {
            long current = balance.get();
            if (current < amount) {
                return Mono.just(new ChargeResponse()
                        .success(false)
                        .newBalance(current)
                        .message("Недостаточно средств"));
            }
            long newBalance = current - amount;
            balance.set(newBalance);
            return Mono.just(new ChargeResponse()
                    .success(true)
                    .newBalance(newBalance)
                    .message("Платеж успешно выполнен"));
        });
    }
}
