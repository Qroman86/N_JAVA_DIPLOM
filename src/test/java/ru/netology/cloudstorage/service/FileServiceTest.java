package ru.netology.cloudstorage.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import ru.netology.cloudstorage.entity.User;
import ru.netology.cloudstorage.repository.CloudFileRepository;
import ru.netology.cloudstorage.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class FileServiceTest {

    @TempDir
    Path tempDir; // Временная папка для замены ./uploads из yaml

    @Test
    void uploadFile_Success_CreatesDirectoryAndFile() throws IOException {
        CloudFileRepository fileRepository = mock(CloudFileRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        FileService fileService = new FileService(fileRepository, userRepository);

        // Внедряем временный путь вместо того, что в yaml
        ReflectionTestUtils.setField(fileService, "storagePath", tempDir.toString());

        String login = "ivan";
        String filename = "test.txt";
        User user = new User();
        user.setLogin(login);
        MockMultipartFile mockFile = new MockMultipartFile("file", filename, "text/plain", "hello".getBytes());

        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));

        fileService.uploadFile(login, filename, mockFile);

        // Проверяем, что создана структура папок как в Docker (storage/login/filename)
        Path expectedPath = tempDir.resolve(login).resolve(filename);
        assertTrue(expectedPath.toFile().exists(), "Файл должен быть создан на диске по пути пользователя");
        verify(fileRepository).save(any());
    }
}
