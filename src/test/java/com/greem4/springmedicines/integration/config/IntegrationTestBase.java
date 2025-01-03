package com.greem4.springmedicines.integration.config;

import com.greem4.springmedicines.dto.JwtResponse;
import com.greem4.springmedicines.dto.LoginRequest;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@Sql(scripts = "classpath:sql/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Testcontainers
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class IntegrationTestBase {

    protected GreenMail greenMail;

    @Autowired
    protected TestRestTemplate testRestTemplate;

    @Container
    protected static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:17-alpine");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
    }

    @BeforeEach
    void setUpGreenMail() {
        var smtpSetup = new ServerSetup(3025, null, "smtp");
        greenMail = new GreenMail(smtpSetup);
        greenMail.setUser("test@localhost", "test");
        greenMail.start();
    }

    @AfterEach
    void tearDown() {
        if (greenMail != null) {
            greenMail.stop();
        }
    }

    protected @NotNull HttpHeaders getHeaders(String username, String password) {
        var jwtResponse = authenticate(username, password);
        var headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.token());
        return headers;
    }

    protected @NotNull HttpEntity<Object> getAuth(String username, String password) {
        var headers = getHeaders(username, password);
        return new HttpEntity<>(null, headers);
    }

    protected @NotNull HttpHeaders getHeadersAdmin() {
        return getHeaders("admin", "admin");
    }

    protected @NotNull HttpHeaders getHeadersUser() {
        return getHeaders("user", "user");
    }

    protected JwtResponse authenticate(String username, String password) {
        var loginRequest = new LoginRequest(username, password);

        var response = testRestTemplate.postForEntity(
                "/api/auth/login",
                new HttpEntity<>(loginRequest, getHttpHeaders()),
                JwtResponse.class
        );

        return Objects.requireNonNull(response.getBody());
    }

    protected static @NotNull HttpHeaders getHttpHeaders() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
