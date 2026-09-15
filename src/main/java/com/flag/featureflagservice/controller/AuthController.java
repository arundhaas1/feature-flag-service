package com.flag.featureflagservice.controller;

import com.flag.featureflagservice.controller.input.LoginRequest;
import com.flag.featureflagservice.controller.output.LoginResponse;
import com.flag.featureflagservice.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final String ROLE_PREFIX = "^ROLE_";

    //Injected after Bean created in Security Config
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication authenticated = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        String role = authenticated.getAuthorities().iterator().next()
                .getAuthority().replaceFirst(ROLE_PREFIX, "");

        return new LoginResponse(
                jwtService.issue(authenticated.getName(), role),
                authenticated.getName(),
                role,
                jwtService.getExpiresInSeconds());
    }
}
