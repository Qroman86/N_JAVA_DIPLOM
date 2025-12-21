package ru.netology.cloudstorage.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class DtoSerializationTest {

    @Autowired
    private JacksonTester<AuthResponse> authResponseJson;

    @Autowired
    private JacksonTester<LoginResponse> loginResponseJson;

    @Test
    void testAuthResponseFieldName() throws Exception {
        AuthResponse response = new AuthResponse("test-token-123");

        // Проверяем, что поле называется "auth-token", а не "authToken"
        assertThat(authResponseJson.write(response))
                .hasJsonPathStringValue("@['auth-token']");

        assertThat(authResponseJson.write(response))
                .extractingJsonPathStringValue("@['auth-token']")
                .isEqualTo("test-token-123");
    }

    @Test
    void testFileListResponse() throws Exception {
        FileListResponse response = new FileListResponse("image.png", 1024L);
        assertThat(response.filename()).isEqualTo("image.png");
        assertThat(response.size()).isEqualTo(1024L);
    }
}
