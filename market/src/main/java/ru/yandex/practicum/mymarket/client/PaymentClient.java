package ru.yandex.practicum.mymarket.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.ya.domain.BalanceResponse;
import ru.ya.domain.ChargeRequest;
import ru.ya.domain.ChargeResponse;

@Component
public class PaymentClient {

    private final WebClient webClient;

    public PaymentClient(WebClient.Builder builder,
                         @Value("${payment.service.url:http://localhost:8081}") String paymentUrl) {
        this.webClient = builder.baseUrl(paymentUrl).build();
    }

    public Mono<BalanceResponse> getBalance() {
        return webClient.get()
                .uri("/api/v1/payment/balance")
                .retrieve()
                .bodyToMono(BalanceResponse.class)
                .onErrorResume(e -> Mono.empty()); // если сервис недоступен – возвращаем пустой Mono
    }

    public Mono<ChargeResponse> charge(Long amount, Long orderId) {
        ChargeRequest request = new ChargeRequest();
        request.setAmount(amount);
        request.setOrderId(orderId);
        return webClient.post()
                .uri("/api/v1/payment/charge")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChargeResponse.class)
                .onErrorResume(e -> Mono.just(new ChargeResponse()
                        .success(false)
                        .message("Сервис платежей временно недоступен")));
    }
}