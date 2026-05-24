package ru.yandex.practicum.mymarket.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
public class Paging {

    private boolean hasPrev;
    private boolean hasNext;
    private int pageSize;
    private int pageNumber;
}
