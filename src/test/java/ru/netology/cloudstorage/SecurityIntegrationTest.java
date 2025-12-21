package ru.netology.cloudstorage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class SecurityIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void loginEndPoint_ShouldBePermitAll() {
        // Пытаемся отправить пустой POST на логин. Должны получить 400 (Bad Request), а не 403 (Forbidden)
        ResponseEntity<String> response = restTemplate.postForEntity("/cloud/login", null, String.class);
        assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void listEndPoint_WithoutToken_ShouldBeForbidden() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cloud/list", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void corsOptionsRequest_ShouldReturnOk() {
        // Проверяем Preflight запрос (OPTIONS), который делает браузер перед запросом
        HttpHeaders headers = new HttpHeaders();
        headers.add("Origin", "http://localhost:8081");
        headers.add("Access-Control-Request-Method", "GET");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Void> response = restTemplate.exchange("/cloud/list", HttpMethod.OPTIONS, entity, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getAccessControlAllowOrigin()).isEqualTo("http://localhost:8081");
    }
}