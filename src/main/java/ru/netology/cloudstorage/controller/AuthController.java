package ru.netology.cloudstorage.controller;

import org.springframework.http.ResponseEntity;
import ru.netology.cloudstorage.dto.AuthResponse;
import ru.netology.cloudstorage.dto.LoginRequest;
import ru.netology.cloudstorage.dto.LoginResponse;
import ru.netology.cloudstorage.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cloud")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint для авторизации пользователя.
     * @param request DTO с логином и паролем
     * @return 200 OK с токеном или 401 Unauthorized
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        String token = authService.authenticate(
                request.getLogin(),
                request.getPassword()
        );

        if (token == null) {
            return ResponseEntity.status(401).build(); // Неверные credentials
        }

        return ResponseEntity.ok(new AuthResponse(token));
    }

    /**
     * Endpoint для проверки токена (опционально).
     * @param authToken токен в заголовке
     * @return 200 OK если токен валиден, иначе 401
     */
    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(@RequestHeader("auth-token") String authToken) {
        if (authService.validateToken(authToken)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(401).build();
    }

    /**
     * Endpoint для выхода из системы.
     * @param authToken токен в заголовке
     * @return 204 No Content
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("auth-token") String authToken) {
        authService.logout(authToken);
        return ResponseEntity.noContent().build();
    }
}