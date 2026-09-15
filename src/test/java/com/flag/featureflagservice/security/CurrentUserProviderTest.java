package com.flag.featureflagservice.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static com.flag.featureflagservice.TestConstants.AUTHORITY_ADMIN;
import static com.flag.featureflagservice.TestConstants.SYSTEM_USER;
import static com.flag.featureflagservice.TestConstants.USERNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("CurrentUserProvider")
class CurrentUserProviderTest {

    private final CurrentUserProvider currentUserProvider = new CurrentUserProvider();

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Given an authenticated caller, when asked, then their username is returned")
    void givenAuthenticatedCaller_whenCurrentUsername_thenReturnsUsername() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(USERNAME, null,
                        List.of(new SimpleGrantedAuthority(AUTHORITY_ADMIN))));

        assertEquals(USERNAME, currentUserProvider.currentUsername());
    }

    @Test
    @DisplayName("Given no authentication, when asked, then the system fallback is returned")
    void givenNoAuthentication_whenCurrentUsername_thenReturnsSystemFallback() {
        assertEquals(SYSTEM_USER, currentUserProvider.currentUsername());
    }

    @Test
    @DisplayName("Given an anonymous caller, when asked, then the system fallback is returned")
    void givenAnonymousCaller_whenCurrentUsername_thenReturnsSystemFallback() {
        SecurityContextHolder.getContext().setAuthentication(
                new AnonymousAuthenticationToken("key", "anonymousUser",
                        List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));

        assertEquals(SYSTEM_USER, currentUserProvider.currentUsername());
    }
}
