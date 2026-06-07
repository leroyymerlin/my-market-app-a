package ru.yandex.practicum.mymarket.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.repository.ItemRepository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(properties = {"spring.main.lazy-initialization=true"})
@Transactional
class FileServiceTest {

    @Autowired
    private FileService fileService;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void upload_ShouldCreateDirectoryAndSaveFileAndCallRepository() {
        itemRepository.deleteAll();

        Item item = new Item();
        item.setId(1L);
        item.setTitle("Тестовый товар");
        item.setPrice(100L);
        item.setCount(0);
        Long  existingItemId = itemRepository.save(item).getId();
        byte[] imageBytes = {1, 2, 3, 4, 5};
        fileService.upload(existingItemId, imageBytes);

        Item updated = itemRepository.findById(existingItemId).orElseThrow();
        assertThat(updated.getImgPath()).isEqualTo(java.util.Arrays.toString(imageBytes));
    }
}