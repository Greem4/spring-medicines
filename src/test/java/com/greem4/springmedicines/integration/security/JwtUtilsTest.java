package com.greem4.springmedicines.integration.security;

import com.greem4.springmedicines.integration.config.IntegrationTestBase;
import com.greem4.springmedicines.util.security.JwtUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;


class JwtUtilsTest extends IntegrationTestBase {

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    void generateJwtToken() {
        var body = authenticate("admin", "admin");

        assertThat(body).isNotNull();
        assertThat(body.type()).isEqualTo("Bearer");
        assertThat(body.token().split("\\.")).hasSize(3);
    }

    @Test
    void getUserNameFromJwtToken() {
        var body = authenticate("admin", "admin");

        assertThat(body.username()).isEqualTo("admin");
    }

    @Test
    void validateJwtTokenWithValidToken() {
        var jwtResponse = authenticate("admin", "admin");
        var validToken = jwtResponse.token();
        var isValid = jwtUtils.validateJwtToken(validToken);
        assertThat(isValid).isTrue();
    }

    @Test
    void validateJwtTokenWithInvalidSignature() {
        SecretKey anotherKey = Keys.
                hmacShaKeyFor("aVeryS+fddfdeDESDeeqdr-tKeyForJWTAuthentica=".
                        getBytes(StandardCharsets.UTF_8));
        String tokenWithInvalidSignature = Jwts.builder()
                .subject("admin")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(anotherKey)
                .compact();
        var hasInvalidSignature = jwtUtils.validateJwtToken(tokenWithInvalidSignature);
        assertThat(hasInvalidSignature).isFalse();
    }

    @Test
    void validateJwtTokenWithInvalidToken() {
        var invalidToken = "invalid.token.value";
        var isInvalid = jwtUtils.validateJwtToken(invalidToken);
        assertThat(isInvalid).isFalse();

        String expiredToken = Jwts.builder()
                .subject("expiredUser")
                .issuedAt(new Date(System.currentTimeMillis() - 10000))
                .expiration(new Date(System.currentTimeMillis() - 500000))
                .signWith(jwtUtils.getKey())
                .compact();
        var isExpired = jwtUtils.validateJwtToken(expiredToken);
        assertThat(isExpired).isFalse();
    }

    @Test
    void validateJwtTokenFalse() {
        String invalidToken = "invalid.token.value";

        boolean isInvalid = jwtUtils.validateJwtToken(invalidToken);

        assertThat(isInvalid).isFalse();
    }


    @Test
    void validateJwtTokenAdmin() {
        var jwtResponse = authenticate("admin", "admin");
        var isValid = jwtUtils.validateJwtToken(jwtResponse.token());
        assertThat(isValid).isTrue();
    }

    @Test
    void validateJwtTokenUser() {
        var jwtResponse = authenticate("user", "user");
        var isValid = jwtUtils.validateJwtToken(jwtResponse.token());
        assertThat(isValid).isTrue();
    }
}
