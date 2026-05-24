package ru.yandex.practicum.mymarket.repository;

import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с файлами.
 */
@Repository
public interface FileRepository {

    /**
     * Добавление изображения.
     *
     * @param id - идентификатор изображения
     * @param imageData изображение типа массив байт
     * @param contentType заголовок
     */
    void addImage(int id, byte[] imageData, String contentType);
}
