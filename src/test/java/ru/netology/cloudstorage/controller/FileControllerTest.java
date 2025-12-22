package ru.netology.cloudstorage.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.cloudstorage.entity.CloudFile;
import ru.netology.cloudstorage.repository.CloudFileRepository;
import ru.netology.cloudstorage.service.AuthService;
import ru.netology.cloudstorage.service.FileService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
@AutoConfigureMockMvc(addFilters = false)
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @MockBean
    private AuthService authService;

    @MockBean
    private CloudFileRepository fileRepository;

    private final String token = "Bearer valid-token";
    private final String cleanToken = "valid-token";
    private final String login = "user";

    @Test
    @DisplayName("GET /list: Успешное получение списка")
    void list_ShouldReturnOk() throws Exception {
        when(authService.getLoginByToken(cleanToken)).thenReturn(login);
        when(fileService.getFileList(login, 3)).thenReturn(List.of());

        mockMvc.perform(get("/cloud/list")
                        .header("auth-token", token)
                        .param("limit", "3"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /file: Успешная загрузка файла")
    void upload_ShouldReturnOk() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());
        when(authService.getLoginByToken(cleanToken)).thenReturn(login);

        mockMvc.perform(multipart("/cloud/file")
                        .file(file)
                        .header("auth-token", token)
                        .param("filename", "test.txt"))
                .andExpect(status().isOk());

        verify(fileService).uploadFile(eq(login), eq("test.txt"), any());
    }

    @Test
    @DisplayName("DELETE /file: Успешное удаление")
    void delete_ShouldReturnOk() throws Exception {
        when(authService.getLoginByToken(cleanToken)).thenReturn(login);

        mockMvc.perform(delete("/cloud/file")
                        .header("auth-token", token)
                        .param("filename", "test.txt"))
                .andExpect(status().isOk());

        verify(fileService).deleteFile(login, "test.txt");
    }

    @Test
    @DisplayName("GET /file: Успешное скачивание файла")
    void download_ShouldReturnFile() throws Exception {
        byte[] content = "file data".getBytes();
        CloudFile meta = new CloudFile();
        meta.setContentType("text/plain");
        meta.setOriginalName("original.txt");

        when(authService.getLoginByToken(cleanToken)).thenReturn(login);
        when(fileService.downloadFile(login, "test.txt")).thenReturn(new ByteArrayResource(content));
        when(fileRepository.findByOwnerLoginAndFilename(login, "test.txt")).thenReturn(Optional.of(meta));

        mockMvc.perform(get("/cloud/file")
                        .header("auth-token", token)
                        .param("filename", "test.txt"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"original.txt\""))
                .andExpect(content().bytes(content));
    }

    @Test
    @DisplayName("PUT /file: Успешное переименование")
    void rename_ShouldReturnOk() throws Exception {
        when(authService.getLoginByToken(cleanToken)).thenReturn(login);
        String jsonBody = "{\"filename\": \"new_name.txt\"}";

        mockMvc.perform(put("/cloud/file")
                        .header("auth-token", token)
                        .param("filename", "old_name.txt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk());

        verify(fileService).renameFile(login, "old_name.txt", "new_name.txt");
    }
}