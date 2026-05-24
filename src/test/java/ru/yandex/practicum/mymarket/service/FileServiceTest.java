package ru.yandex.practicum.mymarket.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.mymarket.repository.FileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@SpringBootTest(properties = {"spring.main.lazy-initialization=true"})
class FileServiceTest {

    @MockitoBean
    private FileRepository fileRepository;

    @Autowired
    private FileService fileService;

    @AfterEach
    void tearDown() throws IOException {
        Path uploadDir = Paths.get(FileService.UPLOAD_DIR);
        if (Files.exists(uploadDir)) {
            Files.walk(uploadDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {

                        }
                    });
        }
    }

    @Test
    void upload_ShouldCreateDirectoryAndSaveFileAndCallRepository() throws IOException {
        int postId = 1;
        String fileName = "test.png";
        byte[] fileContent = "image data".getBytes();
        String contentType = "image/png";

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getBytes()).thenReturn(fileContent);
        when(file.getContentType()).thenReturn(contentType);

        String result = fileService.upload(postId, file);

        assertEquals(fileName, result);

        Path expectedPath = Paths.get(FileService.UPLOAD_DIR, fileName);
        verify(file, times(1)).transferTo(expectedPath);

        verify(fileRepository, times(1)).addImage(eq(postId), eq(fileContent), eq(contentType));
    }

    @Test
    void upload_WhenDirectoryAlreadyExists_ShouldNotRecreateAndStillWork() throws IOException {
        Path uploadDir = Paths.get(FileService.UPLOAD_DIR);
        Files.createDirectories(uploadDir);

        int postId = 2;
        String fileName = "existing.png";
        byte[] fileContent = "data".getBytes();
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getBytes()).thenReturn(fileContent);
        when(file.getContentType()).thenReturn("image/png");

        String result = fileService.upload(postId, file);

        assertEquals(fileName, result);
        verify(fileRepository).addImage(eq(postId), eq(fileContent), eq("image/png"));
    }

    @Test
    void upload_WhenIOExceptionOccurs_ShouldThrowRuntimeException() throws IOException {
        int postId = 1;
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("file.txt");
        when(file.getBytes()).thenThrow(new IOException("Simulated IO error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> fileService.upload(postId, file));
        assertTrue(exception.getMessage().contains("Simulated IO error"));
        verify(fileRepository, never()).addImage(anyInt(), any(), any());
    }
}