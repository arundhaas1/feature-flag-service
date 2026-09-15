package com.flag.featureflagservice.security;

import tools.jackson.databind.ObjectMapper;
import com.flag.featureflagservice.exception.GlobalExceptionHandler.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * Writes security-filter failures in the same JSON shape {@code GlobalExceptionHandler} uses.
 *
 * <p>Failures raised inside the Spring Security filter chain never reach {@code @RestControllerAdvice},
 * so without this the API would answer with two different error formats depending on where the
 * request died.
 */
@Component
public class ApiErrorWriter {

    private final ObjectMapper objectMapper;

    public ApiErrorWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void write(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getWriter(), new ErrorResponse(message, Instant.now()));
    }
}
