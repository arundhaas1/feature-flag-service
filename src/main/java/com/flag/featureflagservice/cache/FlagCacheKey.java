package com.flag.featureflagservice.cache;

/**
 * Identifies one cached answer.
 *
 * <p>Keyed by the same three values the evaluation API takes, rather than by database ids, so a
 * lookup needs no query to build its key.
 */
public record FlagCacheKey(String flagKey, String applicationName, String environmentName) {
}
