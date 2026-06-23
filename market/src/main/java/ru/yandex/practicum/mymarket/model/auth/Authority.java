package ru.yandex.practicum.mymarket.model.auth;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("authorities")
public class Authority {
    @Id
    private Long id;
    private String username;
    private String authority;
}
