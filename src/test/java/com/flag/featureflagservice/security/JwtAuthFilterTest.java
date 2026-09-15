package com.flag.featureflagservice.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static com.flag.featureflagservice.TestConstants.AUTHORITY_ADMIN;
import static com.flag.featureflagservice.TestConstants.AUTHORIZATION_HEADER;
import static com.flag.featureflagservice.TestConstants.BEARER_PREFIX;
import static com.flag.featureflagservice.TestConstants.ROLE_ADMIN;
import static com.flag.featureflagservice.TestConstants.TOKEN;
import static com.flag.featureflagservice.TestConstants.USERNAME;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthFilter")
class JwtAuthFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Given a valid bearer token, when filtered, then the principal is authenticated")
    void givenValidBearerToken_whenFilter_thenAuthenticationIsSetInContext() throws Exception {
        request.addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + TOKEN);
        when(jwtService.parse(TOKEN)).thenReturn(Optional.of(new JwtPrincipal(USERNAME, ROLE_ADMIN)));

        jwtAuthFilter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertAll(
                () -> assertEquals(USERNAME, authentication.getName()),
                () -> assertTrue(authentication.getAuthorities().stream()
                        .anyMatch(granted -> AUTHORITY_ADMIN.equals(granted.getAuthority()))),
                () -> verify(filterChain).doFilter(request, response)
        );
    }

    @Test
    @DisplayName("Given no Authorization header, when filtered, then the context stays empty")
    void givenNoAuthorizationHeader_whenFilter_thenContextRemainsEmpty() throws Exception {
        jwtAuthFilter.doFilter(request, response, filterChain);

        assertAll(
                () -> assertNull(SecurityContextHolder.getContext().getAuthentication()),
                () -> verify(filterChain).doFilter(request, response),
                () -> verifyNoInteractions(jwtService)
        );
    }

    @Test
    @DisplayName("Given a Basic auth header, when filtered, then JWT parsing is skipped so Basic still works")
    void givenNonBearerHeader_whenFilter_thenJwtParsingIsSkipped() throws Exception {
        request.addHeader(AUTHORIZATION_HEADER, "Basic YWRtaW46YWRtaW4xMjM=");

        jwtAuthFilter.doFilter(request, response, filterChain);

        assertAll(
                () -> assertNull(SecurityContextHolder.getContext().getAuthentication()),
                () -> verify(filterChain).doFilter(request, response),
                () -> verifyNoInteractions(jwtService)
        );
    }

    @Test
    @DisplayName("Given an invalid bearer token, when filtered, then the request continues unauthenticated")
    void givenInvalidBearerToken_whenFilter_thenContextRemainsEmpty() throws Exception {
        request.addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + TOKEN);
        when(jwtService.parse(TOKEN)).thenReturn(Optional.empty());

        jwtAuthFilter.doFilter(request, response, filterChain);

        assertAll(
                () -> assertNull(SecurityContextHolder.getContext().getAuthentication()),
                () -> verify(filterChain).doFilter(request, response)
        );
    }
}
