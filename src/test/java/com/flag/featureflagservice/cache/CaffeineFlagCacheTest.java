package com.flag.featureflagservice.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.flag.featureflagservice.TestConstants.CACHE_MAX_SIZE;
import static com.flag.featureflagservice.TestConstants.CACHE_TTL_SECONDS;
import static com.flag.featureflagservice.TestConstants.DEFAULT_APP;
import static com.flag.featureflagservice.TestConstants.ENVIRONMENT_NAME;
import static com.flag.featureflagservice.TestConstants.FLAG_KEY;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("CaffeineFlagCache")
class CaffeineFlagCacheTest {

    private static final FlagCacheKey KEY =
            new FlagCacheKey(FLAG_KEY, DEFAULT_APP, ENVIRONMENT_NAME);
    private static final FlagCacheKey OTHER_KEY =
            new FlagCacheKey(FLAG_KEY, DEFAULT_APP, "Production");

    private FlagCache flagCache;

    @BeforeEach
    void setUp() {
        flagCache = new CaffeineFlagCache(CACHE_TTL_SECONDS, CACHE_MAX_SIZE);
    }

    @Test
    @DisplayName("Given a stored value, when looked up, then it is returned")
    void givenStoredValue_whenLookup_thenReturnsIt() {
        flagCache.store(KEY, true);

        assertEquals(true, flagCache.lookup(KEY).orElseThrow());
    }

    @Test
    @DisplayName("Given a key never stored, when looked up, then nothing is returned")
    void givenUnknownKey_whenLookup_thenReturnsEmpty() {
        assertTrue(flagCache.lookup(KEY).isEmpty());
    }

    @Test
    @DisplayName("Given a stored false, when looked up, then false is returned rather than a miss")
    void givenStoredFalse_whenLookup_thenReturnsFalseNotMiss() {
        flagCache.store(KEY, false);

        assertAll(
                () -> assertTrue(flagCache.lookup(KEY).isPresent()),
                () -> assertEquals(false, flagCache.lookup(KEY).orElseThrow())
        );
    }

    @Test
    @DisplayName("Given two environments, when one is evicted, then the other survives")
    void givenTwoEnvironments_whenOneEvicted_thenTheOtherSurvives() {
        flagCache.store(KEY, true);
        flagCache.store(OTHER_KEY, true);

        flagCache.evict(KEY);

        assertAll(
                () -> assertTrue(flagCache.lookup(KEY).isEmpty()),
                () -> assertTrue(flagCache.lookup(OTHER_KEY).isPresent())
        );
    }

    @Test
    @DisplayName("Given stored values, when the cache is cleared, then none remain")
    void givenStoredValues_whenEvictAll_thenNoneRemain() {
        flagCache.store(KEY, true);
        flagCache.store(OTHER_KEY, false);

        flagCache.evictAll();

        assertAll(
                () -> assertTrue(flagCache.lookup(KEY).isEmpty()),
                () -> assertTrue(flagCache.lookup(OTHER_KEY).isEmpty())
        );
    }
}
