package com.greem4.springmedicines.security;

import com.greem4.springmedicines.database.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private final String jwtSecret = "mySecretKey123456789012345678901234567890";
    private final int jwtExpirationMs = 3600000; // 1 hour

    @BeforeEach
    void setUp() throws Exception {
        jwtUtils = new JwtUtils();

        Field secretField = JwtUtils.class.getDeclaredField("jwtSecret");
        secretField.setAccessible(true);
        secretField.set(jwtUtils, jwtSecret);

        Field expirationField = JwtUtils.class.getDeclaredField("jwtExpirationMs");
        expirationField.setAccessible(true);
        expirationField.set(jwtUtils, jwtExpirationMs);

        jwtUtils.init();
    }

//    @Test
//    void testGenerateJwtToken() {
//        // Arrange
//        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
//        Authentication authentication = mock(Authentication.class);
//        when(authentication.getPrincipal()).thenReturn(userDetails);
//
//        // Act
//        String token = jwtUtils.generateJwtToken(authentication);
//
//        // Assert
//        assertThat(token).isNotNull();
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//        assertThat(claims.get("username")).isEqualTo("testuser");
//    }
}
