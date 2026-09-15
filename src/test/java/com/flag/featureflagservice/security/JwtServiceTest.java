package com.flag.featureflagservice.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.flag.featureflagservice.TestConstants.ALREADY_EXPIRED_MINUTES;
import static com.flag.featureflagservice.TestConstants.EXPIRY_MINUTES;
import static com.flag.featureflagservice.TestConstants.MALFORMED_TOKEN;
import static com.flag.featureflagservice.TestConstants.OTHER_SECRET;
import static com.flag.featureflagservice.TestConstants.ROLE_ADMIN;
import static com.flag.featureflagservice.TestConstants.SECRET;
import static com.flag.featureflagservice.TestConstants.SHORT_SECRET;
import static com.flag.featureflagservice.TestConstants.USERNAME;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("JwtService")
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, EXPIRY_MINUTES);
    }

    @Test
    @DisplayName("Given valid credentials, when issue, then the token parses back to the same principal")
    void givenValidCredentials_whenIssue_thenTokenParsesBackToSamePrincipal() {
        String token = jwtService.issue(USERNAME, ROLE_ADMIN);

        Optional<JwtPrincipal> principal = jwtService.parse(token);

        assertAll(
                () -> assertTrue(principal.isPresent()),
                () -> assertEquals(USERNAME, principal.orElseThrow().username()),
                () -> assertEquals(ROLE_ADMIN, principal.orElseThrow().role())
        );
    }

    @Test
    @DisplayName("Given a tampered token, when parse, then no principal is returned")
    void givenTamperedToken_whenParse_thenReturnsEmpty() {
        String token = jwtService.issue(USERNAME, ROLE_ADMIN);
        String tampered = tamperPayload(token);

        assertTrue(jwtService.parse(tampered).isEmpty());
    }

    @Test
    @DisplayName("Given a token signed with another secret, when parse, then no principal is returned")
    void givenTokenFromAnotherSecret_whenParse_thenReturnsEmpty() {
        JwtService foreignIssuer = new JwtService(OTHER_SECRET, EXPIRY_MINUTES);
        String foreignToken = foreignIssuer.issue(USERNAME, ROLE_ADMIN);

        assertTrue(jwtService.parse(foreignToken).isEmpty());
    }

    @Test
    @DisplayName("Given an expired token, when parse, then no principal is returned")
    void givenExpiredToken_whenParse_thenReturnsEmpty() {
        JwtService expiredIssuer = new JwtService(SECRET, ALREADY_EXPIRED_MINUTES);
        String expiredToken = expiredIssuer.issue(USERNAME, ROLE_ADMIN);

        assertTrue(jwtService.parse(expiredToken).isEmpty());
    }

    @Test
    @DisplayName("Given a malformed or absent token, when parse, then no principal is returned")
    void givenMalformedToken_whenParse_thenReturnsEmpty() {
        assertAll(
                () -> assertTrue(jwtService.parse(MALFORMED_TOKEN).isEmpty()),
                () -> assertTrue(jwtService.parse("").isEmpty()),
                () -> assertTrue(jwtService.parse(null).isEmpty())
        );
    }

    @Test
    @DisplayName("Given a secret below the HS256 minimum, when constructed, then startup fails fast")
    void givenShortSecret_whenConstructed_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new JwtService(SHORT_SECRET, EXPIRY_MINUTES));
    }

    @Test
    @DisplayName("Given a configured expiry, when asked, then it is reported in seconds")
    void givenConfiguredExpiry_whenGetExpiresInSeconds_thenReturnsSeconds() {
        assertEquals(EXPIRY_MINUTES * 60, jwtService.getExpiresInSeconds());
    }

    /**
     * Alters a character inside the payload segment.
     *
     * <p>Deliberately not the final character of the signature: a 256-bit HMAC base64url-encodes
     * to 43 characters whose last character carries only 4 significant bits, so flipping its two
     * low padding bits decodes to identical bytes and the token still verifies. Measured, that
     * made a last-character tamper test pass 469 times in 500 and fail the other 31.
     */
    private String tamperPayload(String token) {
        int index = token.indexOf('.') + 3;
        char replacement = token.charAt(index) == 'X' ? 'Y' : 'X';
        return token.substring(0, index) + replacement + token.substring(index + 1);
    }
}
