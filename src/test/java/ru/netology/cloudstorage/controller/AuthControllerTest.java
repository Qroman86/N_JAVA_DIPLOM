package ru.netology.cloudstorage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.cloudstorage.dto.LoginRequest;
import ru.netology.cloudstorage.service.AuthService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Отключаем Security для чистого Unit-теста
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_ShouldReturnToken_WhenCredentialsValid() throws Exception {
        LoginRequest request = new LoginRequest("user", "pass");
        when(authService.authenticate("user", "pass")).thenReturn("mock-token");

        mockMvc.perform(post("/cloud/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.auth-token").value("mock-token"));
    }

    @Test
    void login_ShouldReturn401_WhenCredentialsInvalid() throws Exception {
        when(authService.authenticate(anyString(), anyString())).thenReturn(null);

        mockMvc.perform(post("/cloud/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"bad\",\"password\":\"bad\"}"))
                .andExpect(status().isUnauthorized());
    }
}
