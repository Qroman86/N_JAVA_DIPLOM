package ru.netology.cloudstorage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.repository.UserRepository;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final Map<String, String> tokenStorage = new ConcurrentHashMap<>();



    public String login(String login, String password) {
        return userRepository.findByLogin(login)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> {
                    String token = UUID.randomUUID().toString();
                    tokenStorage.put(token, login);
                    return token;
                })
                .orElseThrow(() -> new RuntimeException("Bad credentials"));
    }

    public String getLoginByToken(String token) {
        return tokenStorage.get(token);
    }

    public void logout(String token) {
        tokenStorage.remove(token);
    }
}
