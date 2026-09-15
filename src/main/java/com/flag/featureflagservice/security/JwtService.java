package com.flag.featureflagservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * Issues and validates the HS256 tokens used by the admin page.
 *
 * <p>The algorithm is pinned to HS256 rather than left to jjwt, which otherwise picks the
 * strongest algorithm the key length happens to support — making the token header depend on
 * how long someone made the secret.
 *
 * <p>Validation never throws: {@link #parse(String)} returns an empty Optional for anything
 * it will not vouch for, so callers cannot accidentally treat a rejected token as valid.
 */
@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    /** HS256 requires a key of at least 256 bits. */
    private static final int MIN_SECRET_BYTES = 32;

    private static final String ROLE_CLAIM = "role";

    /** Mirrors the fallback in application.properties; public in the repository, so dev-only. */
    private static final String DEV_DEFAULT_SECRET = "dev-only-secret-change-me-at-least-32-bytes-long!";

    private final SecretKey signingKey;
    private final Duration timeToLive;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-minutes:60}") long expirationMinutes) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < MIN_SECRET_BYTES) {
            throw new IllegalArgumentException(
                    "app.jwt.secret must be at least " + MIN_SECRET_BYTES + " bytes for HS256");
        }
        if (DEV_DEFAULT_SECRET.equals(secret)) {
            log.warn("Signing tokens with the built-in development secret, which is public in the "
                    + "repository. Set APP_JWT_SECRET before running this anywhere that matters.");
        }
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.timeToLive = Duration.ofMinutes(expirationMinutes);
    }

    public String issue(String username, String role) {
        Instant issuedAt = Instant.now();
        return Jwts.builder()
                .subject(username)
                .claim(ROLE_CLAIM, role)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(issuedAt.plus(timeToLive)))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    public Optional<JwtPrincipal> parse(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(new JwtPrincipal(claims.getSubject(), claims.get(ROLE_CLAIM, String.class)));
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Rejected token: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    public long getExpiresInSeconds() {
        return timeToLive.toSeconds();
    }
}
