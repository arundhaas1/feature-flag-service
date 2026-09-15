package com.flag.featureflagservice;

/**
 * Shared literals for the test suite, so individual tests stay free of magic values.
 */
public final class TestConstants {

    /** HS256 needs at least 256 bits of key material; this is test-only. */
    public static final String SECRET = "test-secret-key-that-is-long-enough-for-hs256!";
    public static final String OTHER_SECRET = "a-completely-different-secret-key-for-hs256!!";
    public static final String SHORT_SECRET = "too-short";

    public static final long EXPIRY_MINUTES = 60L;
    public static final long ALREADY_EXPIRED_MINUTES = -1L;

    public static final String USERNAME = "admin";
    public static final String PASSWORD = "admin123";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_VIEWER = "VIEWER";
    public static final String AUTHORITY_ADMIN = "ROLE_ADMIN";

    public static final String TOKEN = "a.test.token";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String MALFORMED_TOKEN = "not-a-jwt";

    public static final String PROTECTED_URL = "/api/v1/demo/flags";
    public static final String DELETE_URL = "/api/v1/demo/flags/1";
    public static final String LOGIN_URL = "/auth/login";
    public static final String WWW_AUTHENTICATE = "WWW-Authenticate";

    private TestConstants() {
    }
}
