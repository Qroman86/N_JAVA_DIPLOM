package ru.netology.cloudstorage.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CorsSecurityTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCorsHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Origin", "http://localhost:8081");
        headers.add("Access-Control-Request-Method", "POST");
        headers.add("Access-Control-Request-Headers", "auth-token");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Void> response = restTemplate.exchange("/cloud/login", HttpMethod.OPTIONS, entity, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getAccessControlAllowOrigin()).isEqualTo("http://localhost:8081");
        assertThat(response.getHeaders().getAccessControlExposeHeaders()).contains("auth-token");
    }
}
