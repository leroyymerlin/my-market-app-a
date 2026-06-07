package ru.yandex.practicum.mymarket.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "items")
public class Item {

    @Id
    private Long id;

    private String title;
    private String description;

    @Column("img_path")
    private String imgPath;

    private Long price;
    private int count;
}
