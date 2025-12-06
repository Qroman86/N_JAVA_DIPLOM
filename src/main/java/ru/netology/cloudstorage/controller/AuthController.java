package ru.netology.cloudstorage.controller;

import ru.netology.cloudstorage.dto.LoginRequest;
import ru.netology.cloudstorage.dto.LoginResponse;
import ru.netology.cloudstorage.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cloud")
@RequiredArgsConstructor
public class AuthController {

    private AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        String token = authService.login(request.login(), request.password());
        return new LoginResponse(token);
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("auth-token") String token) {
        authService.logout(token);
    }
}
