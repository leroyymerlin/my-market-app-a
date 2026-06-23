package ru.yandex.practicum.mymarket.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.yandex.practicum.mymarket.model.auth.Authority;

public interface AuthorityRepository extends ReactiveCrudRepository<Authority, Long> {
    Flux<Authority> findByUsername(String username);
}
