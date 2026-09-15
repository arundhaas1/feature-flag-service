package com.flag.featureflagservice.controller.output;

/**
 * @param expiresIn token lifetime in seconds, so the client can refresh before it lapses
 */
public record LoginResponse(String token, String username, String role, long expiresIn) {
}
