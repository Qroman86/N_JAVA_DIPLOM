package ru.netology.cloudstorage.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.repository.UserRepository;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.time.Instant;
import java.time.Duration;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Map<String, TokenRecord> activeTokens = new ConcurrentHashMap<>();
    private final Duration tokenLifetime;

    public AuthService(
            UserRepository userRepository,
            @Value("${security.token-lifetime:2h}") Duration tokenLifetime
    ) {
        this.userRepository = userRepository;
        this.tokenLifetime = tokenLifetime;
    }

    /**
     * Аутентифицирует пользователя и возвращает токен.
     * @param login логин
     * @param password пароль
     * @return токен или null при ошибке
     */
    public String authenticate(String login, String password) {
        return userRepository.findByLogin(login)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> {
                    String token = generateToken();
                    activeTokens.put(token, new TokenRecord(login, Instant.now()));
                    return token;
                })
                .orElse(null);
    }

    /**
     * Проверяет валидность токена.
     * @param token токен
     * @return true, если токен действителен
     */
    public boolean validateToken(String token) {
        TokenRecord record = activeTokens.get(token);
        return record != null && !isExpired(record);
    }

    /**
     * Получает логин по токену (только для валидных токенов).
     * @param token токен
     * @return логин или null
     */
    public String getLoginByToken(String token) {
        if (!validateToken(token)) return null;
        return activeTokens.get(token).getLogin();
    }

    /**
     * Аннулирует токен.
     * @param token токен
     */
    public void logout(String token) {
        activeTokens.remove(token);
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    private boolean isExpired(TokenRecord record) {
        return Instant.now().isAfter(record.getCreationTime().plus(tokenLifetime));
    }

    private static class TokenRecord {
        private final String login;
        private final Instant creationTime;

        public TokenRecord(String login, Instant creationTime) {
            this.login = login;
            this.creationTime = creationTime;
        }

        public String getLogin() { return login; }
        public Instant getCreationTime() { return creationTime; }
    }
}