package ru.yandex.practicum.mymarket.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Market {
    private final Long id;
    private final int pageNumber;
    private final String search;
    private final String sort;
}
