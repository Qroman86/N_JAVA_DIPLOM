package ru.netology.cloudstorage.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.netology.cloudstorage.service.AuthService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokenAuthFilterTest {

    private AuthService authService;
    private TokenAuthFilter tokenAuthFilter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        tokenAuthFilter = new TokenAuthFilter(authService);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_WithValidBearerToken_ShouldSetAuthentication() throws Exception {
        String fullToken = "Bearer 12345";
        String cleanToken = "12345";

        when(request.getHeader("auth-token")).thenReturn(fullToken);
        when(authService.validateToken(cleanToken)).thenReturn(true);
        when(authService.getLoginByToken(cleanToken)).thenReturn("user1");

        tokenAuthFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("user1", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithInvalidToken_ShouldNotSetAuthentication() throws Exception {
        when(request.getHeader("auth-token")).thenReturn("invalid");
        when(authService.validateToken(anyString())).thenReturn(false);

        tokenAuthFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @org.junit.jupiter.api.DisplayName("shouldNotFilter: должен вернуть true для пути /cloud/login")
    void shouldNotFilter_LoginPath_ReturnsTrue() {
        // Настраиваем мок запроса на путь логина
        when(request.getServletPath()).thenReturn("/cloud/login");

        // Вызываем тестируемый метод
        boolean result = tokenAuthFilter.shouldNotFilter(request);

        // Проверяем, что фильтр вернет true (т.е. "не фильтровать")
        assertTrue(result);
    }

    @Test
    @org.junit.jupiter.api.DisplayName("shouldNotFilter: должен вернуть false для любого другого пути")
    void shouldNotFilter_OtherPath_ReturnsFalse() {
        // Настраиваем мок запроса на другой защищенный путь
        when(request.getServletPath()).thenReturn("/cloud/list");

        // Вызываем тестируемый метод
        boolean result = tokenAuthFilter.shouldNotFilter(request);

        // Проверяем, что фильтр вернет false (т.е. "нужно фильтровать")
        assertFalse(result);
    }
}
