package ru.yandex.practicum.mymarket.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.repository.AuthorityRepository;
import ru.yandex.practicum.mymarket.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class DatabaseUserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .flatMap(user -> authorityRepository.findByUsername(username)
                        .map(auth -> new SimpleGrantedAuthority(auth.getAuthority()))
                        .collectList()
                        .map(authorities -> User.withUsername(username)
                                .password(user.getPassword())
                                .authorities(authorities)
                                .disabled(!user.isEnabled())
                                .build()
                        )
                );
    }
}
