package ru.yandex.practicum.mymarket.model;

import lombok.Getter;

@Getter
public enum SortType {
    NO("no"), ALPHA("title"), PRICE("price");

    private final String value;

    SortType(String value) {
        this.value = value;
    }

    public static String getSortType(String sort) {
        for(SortType st : SortType.values()) {
            if (st.getValue().equals(sort)) {
                return st.value;
            }
        }
        return sort;
    }
}
