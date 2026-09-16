package com.flag.featureflagservice.evaluation;

import java.util.Map;

/**
 * Everything needed to answer one flag in one environment, for any org.
 *
 * <p>This, rather than a resolved true/false, is what gets cached. Caching the answer would mean
 * one entry per organisation and a cache that never warms; caching the rules keeps the entry
 * count at flags × environments and turns resolution into a map lookup.
 *
 * @param environmentDefault the flag's state for everyone without an override,
 *                           or {@code null} when the flag does not exist in this environment
 * @param organisationOverrides organisation id → the state that org sees instead
 */
public record FlagRules(Boolean environmentDefault, Map<String, Boolean> organisationOverrides) {

    public static FlagRules absent() {
        return new FlagRules(null, Map.of());
    }

    public FlagRules {
        organisationOverrides = Map.copyOf(organisationOverrides);
    }

    /**
     * @param organisationId the org the calling service is acting for, or {@code null} if it
     *                       did not say — in which case only the environment default applies
     */
    public boolean resolve(String organisationId) {
        if (organisationId != null) {
            Boolean override = organisationOverrides.get(organisationId);
            if (override != null) {
                return override;
            }
        }
        return Boolean.TRUE.equals(environmentDefault);
    }

    public boolean flagPresent() {
        return environmentDefault != null;
    }
}
