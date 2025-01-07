package com.greem4.springmedicines.util.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

    @Getter
    private final Key key;
    private final long jwtExpirationMs;

    public JwtUtils(@Value("${jwt.secret}") String jwtSecret,
                    @Value("${jwt.expirationMs}") long jwtExpirationInMs) {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationMs = jwtExpirationInMs;
    }

    public String generateJwtToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key)
                .compact();
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser().
                verifyWith((SecretKey) key).
                build().
                parseSignedClaims(token).
                getPayload().
                getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .clockSkewSeconds(60)
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (JwtException e) {
            log.error("JWT validation failed: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during JWT validation: {}", e.getMessage());
        }
        return false;
    }
}
