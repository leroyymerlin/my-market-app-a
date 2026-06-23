package ru.yandex.practicum.mymarket.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class LoginController {

    @GetMapping("/login")
    public Mono<String> login() {
        return Mono.just("login");
    }
}
