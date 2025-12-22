package ru.netology.cloudstorage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import ru.netology.cloudstorage.dto.FileListResponse;
import ru.netology.cloudstorage.entity.CloudFile;
import ru.netology.cloudstorage.entity.User;
import ru.netology.cloudstorage.repository.CloudFileRepository;
import ru.netology.cloudstorage.repository.UserRepository;
import ru.netology.cloudstorage.service.FileService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileServiceTest {

    @TempDir
    Path tempDir;

    private CloudFileRepository fileRepository;
    private UserRepository userRepository;
    private FileService fileService;

    private final String login = "ivan";
    private final String filename = "test.txt";

    @BeforeEach
    void setUp() {
        fileRepository = mock(CloudFileRepository.class);
        userRepository = mock(UserRepository.class);
        fileService = new FileService(fileRepository, userRepository);
        // Устанавливаем временную папку для каждого теста
        ReflectionTestUtils.setField(fileService, "storagePath", tempDir.toString());
    }

    @Test
    @DisplayName("Загрузка файла: успех")
    void uploadFile_Success() throws IOException {
        User user = new User();
        user.setLogin(login);
        MockMultipartFile mockFile = new MockMultipartFile("file", filename, "text/plain", "content".getBytes());

        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));

        fileService.uploadFile(login, filename, mockFile);

        Path expectedPath = tempDir.resolve(login).resolve(filename);
        assertTrue(Files.exists(expectedPath));
        verify(fileRepository, times(1)).save(any(CloudFile.class));
    }

    @Test
    @DisplayName("Загрузка файла: пользователь не найден")
    void uploadFile_UserNotFound_ThrowsException() {
        when(userRepository.findByLogin(login)).thenReturn(Optional.empty());
        MockMultipartFile mockFile = new MockMultipartFile("file", filename, "text/plain", "content".getBytes());

        assertThrows(RuntimeException.class, () -> fileService.uploadFile(login, filename, mockFile));
    }

    @Test
    @DisplayName("Скачивание файла: успех")
    void downloadFile_Success() throws IOException {
        // Предварительно создаем файл на диске
        Path userDir = tempDir.resolve(login);
        Files.createDirectories(userDir);
        Path filePath = userDir.resolve(filename);
        Files.write(filePath, "file data".getBytes());

        Resource resource = fileService.downloadFile(login, filename);

        assertNotNull(resource);
        assertArrayEquals("file data".getBytes(), resource.getContentAsByteArray());
    }

    @Test
    @DisplayName("Получение списка файлов: успех")
    void getFileList_Success() {
        CloudFile file = new CloudFile();
        file.setFilename(filename);
        file.setSize(100L);

        when(fileRepository.findByOwnerLogin(login)).thenReturn(List.of(file));

        List<FileListResponse> result = fileService.getFileList(login, 3);

        assertEquals(1, result.size());
        assertEquals(filename, result.get(0).filename());
        verify(fileRepository).findByOwnerLogin(login);
    }

    @Test
    @DisplayName("Удаление файла: успех")
    void deleteFile_Success() throws IOException {
        Path userDir = tempDir.resolve(login);
        Files.createDirectories(userDir);
        Path filePath = userDir.resolve(filename);
        Files.write(filePath, "data".getBytes());

        fileService.deleteFile(login, filename);

        assertFalse(Files.exists(filePath));
        verify(fileRepository).deleteByOwnerLoginAndFilename(login, filename);
    }

    @Test
    @DisplayName("Переименование файла: успех")
    void renameFile_Success() throws IOException {
        String newName = "new_name.txt";
        // Создаем файл на диске
        Path userDir = tempDir.resolve(login);
        Files.createDirectories(userDir);
        Path oldPath = userDir.resolve(filename);
        Files.write(oldPath, "data".getBytes());

        // Мокаем БД
        CloudFile cloudFile = new CloudFile();
        cloudFile.setFilename(filename);
        when(fileRepository.findByOwnerLoginAndFilename(login, filename)).thenReturn(Optional.of(cloudFile));

        fileService.renameFile(login, filename, newName);

        // Проверяем диск
        assertFalse(Files.exists(oldPath));
        assertTrue(Files.exists(userDir.resolve(newName)));
        // Проверяем БД
        assertEquals(newName, cloudFile.getFilename());
        verify(fileRepository).save(cloudFile);
    }

    @Test
    @DisplayName("Переименование файла: файл не найден в базе")
    void renameFile_NotFound_ThrowsException() {
        when(fileRepository.findByOwnerLoginAndFilename(anyString(), anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> fileService.renameFile(login, filename, "new.txt"));
    }
}