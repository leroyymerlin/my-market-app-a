package ru.yandex.practicum.mymarket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.repository.ItemRepository;
import java.util.Arrays;

@RequiredArgsConstructor
@Service
public class FileService {

    private final ItemRepository itemRepository;

    public void upload(Long id, byte[] file) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Не найден товар с id: " + id));
        item.setImgPath(Arrays.toString(file));
        itemRepository.save(item);
    }

}
