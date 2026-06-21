package ru.yandex.practicum.mymarket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.repository.ItemRepository;
import java.util.Arrays;

@RequiredArgsConstructor
@Service
public class FileService {

    private final ItemRepository itemRepository;
    private final ItemService itemService;

    public Mono<Void> upload(Long id, byte[] file) {
        return itemRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Не найден товар с id: " + id)))
                .doOnNext(item -> item.setImgPath(Arrays.toString(file)))
                .flatMap(itemRepository::save)
                .then(itemService.clearItemsCache()) // добавляем очистку
                .then();
    }

}
