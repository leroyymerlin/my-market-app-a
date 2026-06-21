package ru.yandex.practicum.mymarket.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.repository.ItemRepository;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {"spring.main.lazy-initialization=true"})
class FileServiceTest {

    @Autowired
    private FileService fileService;

    @MockitoBean
    private ItemRepository itemRepository;

    @Test
    void upload_ShouldCreateDirectoryAndSaveFileAndCallRepository() {
        Item item = new Item(1L, "Тестовый товар", "test", "img", 100L, 0);

        when(itemRepository.findById(1L)).thenReturn(Mono.just(item));
        when(itemRepository.save(any(Item.class))).thenReturn(Mono.just(item));

        byte[] imageBytes = {1, 2, 3, 4, 5};
        fileService.upload(1L, imageBytes)
                .as(StepVerifier::create)
                .verifyComplete();

        Assertions.assertEquals(item.getImgPath(), Arrays.toString(imageBytes));
    }
}