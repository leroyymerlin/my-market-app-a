package ru.yandex.practicum.mymarket.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.practicum.mymarket.repository.AuthorityRepository;
import ru.yandex.practicum.mymarket.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration",
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=test"
})
public class RegisterControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        authorityRepository.deleteAll().block();
        userRepository.deleteAll().block();
    }
    @Test
    void registerForm() {
        webTestClient.get()
                .uri("/register")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("register");
    }

    @Test
    void registerUser() {
        webTestClient.post()
                .uri("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("username=&password=pass")
                .exchange()
                .expectStatus().is4xxClientError();
    }
}