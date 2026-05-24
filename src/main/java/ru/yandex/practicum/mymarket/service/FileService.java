package ru.yandex.practicum.mymarket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.mymarket.repository.FileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class FileService {

    private final FileRepository fileRepository;

    public static final String UPLOAD_DIR = "uploads/";

    public String upload(int id, MultipartFile file) {
        try {
            Path uploadDir = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path filePath = uploadDir.resolve(Objects.requireNonNull(file.getOriginalFilename()));
            file.transferTo(filePath);
            fileRepository.addImage(id, file.getBytes(), file.getContentType());
            return file.getOriginalFilename();

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
