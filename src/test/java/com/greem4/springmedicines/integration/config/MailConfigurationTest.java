package com.greem4.springmedicines.integration.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MailConfigurationTest extends IntegrationTestBase {

    @Autowired
    private Environment env;

    @Test
        // fixme: в чем смысл этого теста? Тестишь сам спринг?)
    void mailPropertiesTest() {
        String host = env.getProperty("spring.mail.host");
        int port = Integer.parseInt(Objects.requireNonNull(env.getProperty("spring.mail.port")));
        String username = env.getProperty("spring.mail.username");
        String password = env.getProperty("spring.mail.password");
        boolean auth = Boolean.parseBoolean(env.getProperty("spring.mail.properties.mail.smtp.auth"));
        boolean starttls = Boolean.parseBoolean(env.getProperty("spring.mail.properties.mail.smtp.starttls.enabled"));

        assertThat(host).isEqualTo("localhost");
        assertThat(port).isEqualTo(3025);
        assertThat(username).isEqualTo("test@localhost");
        assertThat(password).isEqualTo("test");
        assertThat(auth).isTrue();
        assertThat(starttls).isFalse();
    }
}
