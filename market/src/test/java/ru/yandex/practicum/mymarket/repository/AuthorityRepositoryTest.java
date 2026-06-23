package ru.yandex.practicum.mymarket.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ru.yandex.practicum.mymarket.model.auth.Authority;
import ru.yandex.practicum.mymarket.model.auth.User;

@DataR2dbcTest
@ActiveProfiles("test")
class AuthorityRepositoryTest {

    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        authorityRepository.deleteAll().block();
        userRepository.deleteAll().block();

        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("pass");
        user1.setEnabled(true);
        userRepository.save(user1).block();

        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword("pass");
        user2.setEnabled(true);
        userRepository.save(user2).block();
    }

    @Test
    void findByUsername() {
        Authority auth1 = new Authority();
        auth1.setUsername("user1");
        auth1.setAuthority("ROLE_USER");

        Authority auth2 = new Authority();
        auth2.setUsername("user1");
        auth2.setAuthority("ROLE_ADMIN");

        Authority auth3 = new Authority();
        auth3.setUsername("user2");
        auth3.setAuthority("ROLE_USER");

        authorityRepository.saveAll(Flux.just(auth1, auth2, auth3)).blockLast();

        authorityRepository.findByUsername("user1")
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }
}