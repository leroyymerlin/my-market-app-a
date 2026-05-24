package ru.yandex.practicum.mymarket.model;

import lombok.Getter;

@Getter
public enum ActionType {
    MINUS("MINUS"), PLUS("PLUS"), DELETE("DELETE");

    private final String value;

    ActionType(String value) {
        this.value = value;
    }
}
