package ru.netology.cloudstorage;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")  // ← включает application-test.yml
class CloudstorageApplicationTests {

	@Test
	void contextLoads() {
		// Этот тест теперь пройдёт — Testcontainers поднимет БД автоматически
	}
}
