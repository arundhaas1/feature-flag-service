package com.flag.featureflagservice.controller;

import com.flag.featureflagservice.controller.input.LoginRequest;
import com.flag.featureflagservice.controller.output.LoginResponse;
import com.flag.featureflagservice.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static com.flag.featureflagservice.TestConstants.AUTHORITY_ADMIN;
import static com.flag.featureflagservice.TestConstants.EXPIRY_MINUTES;
import static com.flag.featureflagservice.TestConstants.PASSWORD;
import static com.flag.featureflagservice.TestConstants.ROLE_ADMIN;
import static com.flag.featureflagservice.TestConstants.TOKEN;
import static com.flag.featureflagservice.TestConstants.USERNAME;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController")
class AuthControllerTest {

    private static final long EXPIRES_IN_SECONDS = EXPIRY_MINUTES * 60;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("Given valid credentials, when login, then a signed token and the role are returned")
    void givenValidCredentials_whenLogin_thenReturnsTokenAndRole() {
        when(authenticationManager.authenticate(credentials())).thenReturn(authenticated());
        when(jwtService.issue(USERNAME, ROLE_ADMIN)).thenReturn(TOKEN);
        when(jwtService.getExpiresInSeconds()).thenReturn(EXPIRES_IN_SECONDS);

        LoginResponse response = authController.login(loginRequest());

        assertAll(
                () -> assertEquals(TOKEN, response.token()),
                () -> assertEquals(USERNAME, response.username()),
                () -> assertEquals(ROLE_ADMIN, response.role()),
                () -> assertEquals(EXPIRES_IN_SECONDS, response.expiresIn())
        );
    }

    @Test
    @DisplayName("Given bad credentials, when login, then the authentication failure propagates")
    void givenBadCredentials_whenLogin_thenPropagatesAuthenticationException() {
        when(authenticationManager.authenticate(credentials()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authController.login(loginRequest()));
    }

    private LoginRequest loginRequest() {
        LoginRequest request = new LoginRequest();
        request.setUsername(USERNAME);
        request.setPassword(PASSWORD);
        return request;
    }

    private UsernamePasswordAuthenticationToken credentials() {
        return new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD);
    }

    private UsernamePasswordAuthenticationToken authenticated() {
        return new UsernamePasswordAuthenticationToken(USERNAME, null,
                List.of(new SimpleGrantedAuthority(AUTHORITY_ADMIN)));
    }
}
