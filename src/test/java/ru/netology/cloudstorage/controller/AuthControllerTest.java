package ru.netology.cloudstorage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.cloudstorage.dto.LoginRequest;
import ru.netology.cloudstorage.service.AuthService;
import ru.netology.cloudstorage.config.TokenAuthFilter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Отключаем фильтры Spring Security для Unit-теста
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    // В некоторых версиях Spring Boot нужно мокать фильтр, если он инжектится в SecurityConfig
    @MockBean
    private TokenAuthFilter tokenAuthFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /login: Успешный вход")
    void login_ShouldReturnToken_WhenCredentialsValid() throws Exception {
        LoginRequest request = new LoginRequest("user", "pass");
        when(authService.authenticate("user", "pass")).thenReturn("mock-token");

        mockMvc.perform(post("/cloud/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                // Проверяем поле "auth-token", как указано в AuthResponse через @JsonProperty
                .andExpect(jsonPath("$.['auth-token']").value("mock-token"));
    }

    @Test
    @DisplayName("POST /login: Ошибка 401 при неверных данных")
    void login_ShouldReturn401_WhenCredentialsInvalid() throws Exception {
        when(authService.authenticate(anyString(), anyString())).thenReturn(null);

        mockMvc.perform(post("/cloud/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"bad\",\"password\":\"bad\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /validate: Успешная проверка токена")
    void validateToken_ShouldReturnOk_WhenTokenValid() throws Exception {
        String token = "valid-token";
        when(authService.validateToken(token)).thenReturn(true);

        mockMvc.perform(get("/cloud/validate")
                        .header("auth-token", token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /validate: Ошибка 401 при невалидном токене")
    void validateToken_ShouldReturn401_WhenTokenInvalid() throws Exception {
        String token = "invalid-token";
        when(authService.validateToken(token)).thenReturn(false);

        mockMvc.perform(get("/cloud/validate")
                        .header("auth-token", token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /logout: Успешный выход")
    void logout_ShouldReturnNoContent() throws Exception {
        String token = "active-token";

        mockMvc.perform(post("/cloud/logout")
                        .header("auth-token", token))
                .andExpect(status().isNoContent());

        // Проверяем, что метод logout в сервисе действительно был вызван
        verify(authService, times(1)).logout(token);
    }
}