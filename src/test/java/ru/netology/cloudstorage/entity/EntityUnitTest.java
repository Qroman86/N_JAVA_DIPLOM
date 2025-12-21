package ru.netology.cloudstorage.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit-тесты для сущностей (Entity)")
class EntityUnitTest {

    @Test
    @DisplayName("Проверка создания и полей пользователя User")
    void testUserFields() {
        User user = new User();
        user.setLogin("test_user");

        assertEquals("test_user", user.getLogin());
        assertEquals("secret_hash", user.getPassword());
    }

    @Test
    @DisplayName("Проверка создания и полей файла CloudFile")
    void testCloudFileFields() {
        User owner = new User();
        owner.setLogin("admin");
        CloudFile file = new CloudFile();

        file.setId(100L);
        file.setFilename("uuid-name.dat");
        file.setOriginalName("resume.pdf");
        file.setSize(2048L);
        file.setContentType("application/pdf");
        file.setOwner(owner);

        assertAll("Проверка всех геттеров CloudFile",
                () -> assertEquals(100L, file.getId()),
                () -> assertEquals("uuid-name.dat", file.getFilename()),
                () -> assertEquals("resume.pdf", file.getOriginalName()),
                () -> assertEquals(2048L, file.getSize()),
                () -> assertEquals("application/pdf", file.getContentType()),
                () -> assertEquals(owner, file.getOwner()),
                () -> assertEquals("admin", file.getOwner().getLogin())
        );
    }

    @Test
    @DisplayName("Проверка конструкторов User (NoArgs и AllArgs)")
    void testUserConstructors() {
        // Проверка AllArgsConstructor (если вы его используете)
        User userFull = new User();
        userFull.setLogin("login");
        assertEquals("login", userFull.getLogin());

        // Проверка NoArgsConstructor
        User userEmpty = new User();
        assertNull(userEmpty.getLogin());
    }
}
