package ru.yandex.practicum.mymarket.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.model.auth.Authority;
import ru.yandex.practicum.mymarket.model.auth.User;
import ru.yandex.practicum.mymarket.repository.AuthorityRepository;
import ru.yandex.practicum.mymarket.repository.UserRepository;

@RequiredArgsConstructor
@RestController
public class RegisterController {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public Mono<String> registerForm() {
        return Mono.just("register");
    }

    @PostMapping("/register")
    public Mono<String> registerUser(@RequestParam String username,
                                     @RequestParam String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        return userRepository.save(user)
                .flatMap(savedUser -> {
                    Authority auth = new Authority();
                    auth.setUsername(username);
                    auth.setAuthority("ROLE_USER");
                    return authorityRepository.save(auth)
                            .thenReturn("redirect:/login?registered");
                })
                .onErrorResume(e -> Mono.just("redirect:/register?error"));
    }
}
