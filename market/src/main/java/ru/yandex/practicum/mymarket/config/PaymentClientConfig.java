package ru.yandex.practicum.mymarket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import ru.ya.client.ApiClient;
import ru.ya.client.api.DefaultApi;

@Configuration
public class PaymentClientConfig {
    @Bean
    public WebClient webClient(WebClient.Builder builder,
                               ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2Filter =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2Filter.setDefaultClientRegistrationId("market-client");
        return builder.filter(oauth2Filter).build();
    }

    @Bean
    public ApiClient apiClient(WebClient webClient,
                               @Value("${payment.service.url}") String paymentUrl) {
        WebClient customWebClient = webClient.mutate().baseUrl(paymentUrl).build();
        return new ApiClient(customWebClient);
    }

    @Bean
    public DefaultApi defaultApi(ApiClient apiClient) {
        return new DefaultApi(apiClient);
    }
}
