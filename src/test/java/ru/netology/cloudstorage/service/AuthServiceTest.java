package ru.netology.cloudstorage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.netology.cloudstorage.entity.User;
import ru.netology.cloudstorage.repository.UserRepository;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    // Используем тот же энкодер, что и внутри сервиса для подготовки тестовых данных
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        // Инициализируем сервис с коротким временем жизни токена для тестов (2 часа)
        authService = new AuthService(userRepository, Duration.ofHours(2));
    }

    @Test
    @DisplayName("Успешная аутентификация: возвращается UUID токен")
    void authenticate_Success() {
        String login = "user";
        String rawPassword = "pass";
        User user = new User();
        user.setLogin(login);
        user.setPassword(encoder.encode(rawPassword));

        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));

        String token = authService.authenticate(login, rawPassword);

        assertNotNull(token);
        assertDoesNotThrow(() -> java.util.UUID.fromString(token)); // Проверка, что это UUID
        assertTrue(authService.validateToken(token));
        assertEquals(login, authService.getLoginByToken(token));
    }

    @Test
    @DisplayName("Ошибка аутентификации: неверный пароль")
    void authenticate_WrongPassword() {
        String login = "user";
        String rawPassword = "pass";
        User user = new User();
        user.setLogin(login);
        user.setPassword(encoder.encode(rawPassword));

        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));

        String token = authService.authenticate(login, "wrong_pass");

        assertNull(token);
    }

    @Test
    @DisplayName("Ошибка аутентификации: пользователь не найден")
    void authenticate_UserNotFound() {
        when(userRepository.findByLogin("unknown")).thenReturn(Optional.empty());

        String token = authService.authenticate("unknown", "any_pass");

        assertNull(token);
    }

    @Test
    @DisplayName("Проверка валидности: несуществующий токен")
    void validateToken_NotFound() {
        assertFalse(authService.validateToken("non-existent-token"));
    }

    @Test
    @DisplayName("Logout: токен должен удаляться из памяти")
    void logout_Success() {
        String login = "user";
        String rawPassword = "pass";
        User user = new User();
        user.setLogin(login);
        user.setPassword(encoder.encode(rawPassword));
        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));

        String token = authService.authenticate(login, "pass");
        assertTrue(authService.validateToken(token));

        authService.logout(token);

        assertFalse(authService.validateToken(token));
        assertNull(authService.getLoginByToken(token));
    }



    @Test
    @DisplayName("getLoginByToken: возврат null для невалидного токена")
    void getLoginByToken_Invalid() {
        assertNull(authService.getLoginByToken("invalid-token"));
    }
}
