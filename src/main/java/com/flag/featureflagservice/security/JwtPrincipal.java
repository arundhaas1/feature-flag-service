package com.flag.featureflagservice.security;

/**
 * The identity carried inside a validated token: who the caller is and what they may do.
 *
 * <p>Deliberately minimal — anything else the app needs about a user should be looked up
 * from the database rather than trusted from a token the client holds.
 */
public record JwtPrincipal(String username, String role) {
}
