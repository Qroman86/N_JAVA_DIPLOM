package ru.netology.cloudstorage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.cloudstorage.entity.CloudFile;
import ru.netology.cloudstorage.entity.User;
import ru.netology.cloudstorage.repository.CloudFileRepository;
import ru.netology.cloudstorage.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@Transactional
public class RepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CloudFileRepository fileRepository;

    @Test
    void testSaveAndFind() {
        User user = new User();
        user.setLogin("test_user");
        userRepository.save(user);

        CloudFile file = new CloudFile();
        file.setFilename("test.txt");
        file.setOwner(user);
        fileRepository.save(file);

        Optional<CloudFile> found = fileRepository.findByOwnerLoginAndFilename("test_user", "test.txt");
        assertThat(found).isPresent();
    }
}