package com.flag.featureflagservice.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Reports who is making the current request, for stamping {@code createdBy} on new records.
 *
 * <p>Wrapping {@link SecurityContextHolder} keeps the static lookup out of the services and lets
 * them be unit tested by mocking this instead of populating a thread-local.
 */
@Component
public class CurrentUserProvider {

    /** Used for records written outside a request, such as by the startup seeders. */
    public static final String SYSTEM = "system";

    public String currentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean absent = authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken;

        return absent ? SYSTEM : authentication.getName();
    }
}
