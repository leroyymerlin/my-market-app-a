package ru.yandex.practicum.mymarket.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;
import ru.yandex.practicum.mymarket.model.auth.User;

import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @BeforeEach
    void setUp() {
        authorityRepository.deleteAll().block();
        userRepository.deleteAll().block();
    }

    @Test
    void findByUsername() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedpass");
        user.setEnabled(true);
        userRepository.save(user).block();

        userRepository.findByUsername("testuser")
                .as(StepVerifier::create)
                .assertNext(found -> {
                    assertThat(found.getUsername()).isEqualTo("testuser");
                    assertThat(found.getPassword()).isEqualTo("encodedpass");
                    assertThat(found.isEnabled()).isTrue();
                })
                .verifyComplete();
    }
}