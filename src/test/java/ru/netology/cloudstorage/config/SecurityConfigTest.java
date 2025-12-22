package ru.netology.cloudstorage.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import ru.netology.cloudstorage.service.AuthService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(SecurityConfig.class)
            .withBean(TokenAuthFilter.class, () -> mock(TokenAuthFilter.class))
            .withBean(AuthService.class, () -> mock(AuthService.class));

    @Test
    @DisplayName("Проверка создания бина PasswordEncoder")
    void passwordEncoderBeanExists() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(BCryptPasswordEncoder.class);
        });
    }

    @Test
    @DisplayName("Проверка создания основной цепочки фильтров SecurityFilterChain")
    void securityFilterChainBeanExists() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(SecurityFilterChain.class);
        });
    }

    @Test
    @DisplayName("Проверка конфигурации CORS: разрешенные методы и заголовки")
    void corsConfigurationSourceTests() {
        contextRunner.run(context -> {
            assertThat(context).hasBean("corsConfigurationSource");
            CorsConfigurationSource source = context.getBean(CorsConfigurationSource.class);

            // 1. Создаем моки
            jakarta.servlet.http.HttpServletRequest request = mock(jakarta.servlet.http.HttpServletRequest.class);
            jakarta.servlet.http.HttpServletMapping mapping = mock(jakarta.servlet.http.HttpServletMapping.class);

            // 2. Настраиваем маппинг (КРИТИЧНО для Spring Boot 3)
            when(request.getHttpServletMapping()).thenReturn(mapping);
            when(mapping.getMappingMatch()).thenReturn(jakarta.servlet.http.MappingMatch.PATH);
            // Добавляем этот метод, чтобы избежать NPE в UrlPathHelper.ignoreServletPath
            when(mapping.getPattern()).thenReturn("");

            // 3. Настраиваем пути
            when(request.getServletPath()).thenReturn("/any");
            when(request.getRequestURI()).thenReturn("/any");
            when(request.getContextPath()).thenReturn("");

            // 4. Проверяем конфигурацию
            CorsConfiguration config = source.getCorsConfiguration(request);

            assertThat(config).isNotNull();
            assertThat(config.getAllowedOrigins()).contains("http://localhost:8081");
            assertThat(config.getAllowCredentials()).isTrue();
            assertThat(config.getAllowedMethods()).containsAll(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            assertThat(config.getAllowedHeaders()).containsAll(List.of("auth-token", "Content-Type", "Authorization"));
            assertThat(config.getExposedHeaders()).contains("auth-token");
        });
    }
}