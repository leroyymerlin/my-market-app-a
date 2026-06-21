package ru.yandex.practicum.mymarket.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Paging {
    private boolean hasPrev;
    private boolean hasNext;
    private int pageSize;
    private int pageNumber;
}
