package ru.netology.cloudstorage.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.netology.cloudstorage.AbstractIntegrationTest;
import ru.netology.cloudstorage.entity.CloudFile;
import ru.netology.cloudstorage.entity.User;

import static org.assertj.core.api.Assertions.assertThat;

class EntityIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldSaveUserAndFileCorrectly() {
        // Given
        User user = new User();
        user.setLogin("ivan_ivanov");
        entityManager.persist(user);

        CloudFile file = new CloudFile();
        file.setFilename("my_report.pdf");
        file.setOriginalName("report.pdf");
        file.setSize(5000L);
        file.setContentType("application/pdf");
        file.setOwner(user);

        // When
        CloudFile savedFile = entityManager.persistFlushFind(file);

        // Then
        assertThat(savedFile.getId()).isNotNull();
        assertThat(savedFile.getOwner().getLogin()).isEqualTo("ivan_ivanov");
        assertThat(savedFile.getFilename()).isEqualTo("my_report.pdf");
    }

    @Test
    void shouldDeleteFileButKeepUser() {
        // Given
        User user = new User();
        user.setLogin("user2");
        entityManager.persist(user);
        CloudFile file = new CloudFile();
        file.setFilename("delete_me.txt");
        file.setOwner(user);
        entityManager.persist(file);
        entityManager.flush();

        // When
        entityManager.remove(file);
        entityManager.flush();

        // Then
        User foundUser = entityManager.find(User.class, "user2");
        assertThat(foundUser).isNotNull(); // Пользователь остался
        CloudFile foundFile = entityManager.find(CloudFile.class, file.getId());
        assertThat(foundFile).isNull(); // Файл удален
    }
}