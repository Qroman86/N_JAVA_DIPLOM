package ru.netology.cloudstorage.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.cloudstorage.repository.CloudFileRepository;
import ru.netology.cloudstorage.service.AuthService;
import ru.netology.cloudstorage.service.FileService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    void list_ShouldReturnOk_WhenTokenIsValid() throws Exception {
        String token = "Bearer valid-token";
        String cleanToken = "valid-token";

        when(authService.getLoginByToken(cleanToken)).thenReturn("user");
        when(fileService.getFileList("user", 3)).thenReturn(List.of());

        mockMvc.perform(get("/cloud/list")
                        .header("auth-token", token)
                        .param("limit", "3"))
                .andExpect(status().isOk());
    }
}