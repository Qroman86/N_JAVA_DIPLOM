package ru.netology.cloudstorage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.cloudstorage.dto.LoginRequest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class CloudStorageApplicationTests {

	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
			.withDatabaseName("cloud_db")
			.withUsername("cloud")
			.withPassword("cloud");

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
		// Эмулируем путь из Dockerfile/docker-compose
		registry.add("storage.path", () -> "./test-uploads");
	}

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void contextLoads() {
		// Проверка, что контекст приложения со всеми бинами из SecurityConfig поднимается
	}

	@Test
	void testFullCycleLoginAndAccessDenied() {
		// 1. Проверяем, что эндпоинт логина доступен (PermitAll из SecurityConfig)
		LoginRequest loginRequest = new LoginRequest("non-existent", "wrong");
		ResponseEntity<String> response = restTemplate.postForEntity("/cloud/login", loginRequest, String.class);

		// Ожидаем 401 Unauthorized от AuthService, а не 403 Forbidden от Security
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

		// 2. Проверяем, что доступ к списку файлов без токена закрыт (anyRequest().authenticated())
		ResponseEntity<String> listResponse = restTemplate.getForEntity("/cloud/list", String.class);
		assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}
}
