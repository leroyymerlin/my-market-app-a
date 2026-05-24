package ru.yandex.practicum.mymarket.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Order {
    private Long id;
    private Long itemId;
    private List<Item> items;
    private Long totalSum;
    private boolean newOrder;
}
