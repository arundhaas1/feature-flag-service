package com.flag.featureflagservice.cache;

import com.flag.featureflagservice.evaluation.FlagRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

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

    private static final FlagRules ON = new FlagRules(true, Map.of());
    private static final FlagRules OFF = new FlagRules(false, Map.of());

    private FlagCache flagCache;

    @BeforeEach
    void setUp() {
        flagCache = new CaffeineFlagCache(CACHE_TTL_SECONDS, CACHE_MAX_SIZE);
    }

    @Test
    @DisplayName("Given a stored value, when looked up, then it is returned")
    void givenStoredValue_whenLookup_thenReturnsIt() {
        flagCache.store(KEY, ON);

        assertEquals(ON, flagCache.lookup(KEY).orElseThrow());
    }

    @Test
    @DisplayName("Given a key never stored, when looked up, then nothing is returned")
    void givenUnknownKey_whenLookup_thenReturnsEmpty() {
        assertTrue(flagCache.lookup(KEY).isEmpty());
    }

    @Test
    @DisplayName("Given a stored off rule set, when looked up, then it is returned rather than a miss")
    void givenStoredOffRules_whenLookup_thenReturnsRulesNotMiss() {
        flagCache.store(KEY, OFF);

        assertAll(
                () -> assertTrue(flagCache.lookup(KEY).isPresent()),
                () -> assertEquals(OFF, flagCache.lookup(KEY).orElseThrow())
        );
    }

    @Test
    @DisplayName("Given two environments, when one is evicted, then the other survives")
    void givenTwoEnvironments_whenOneEvicted_thenTheOtherSurvives() {
        flagCache.store(KEY, ON);
        flagCache.store(OTHER_KEY, ON);

        flagCache.evict(KEY);

        assertAll(
                () -> assertTrue(flagCache.lookup(KEY).isEmpty()),
                () -> assertTrue(flagCache.lookup(OTHER_KEY).isPresent())
        );
    }

    @Test
    @DisplayName("Given stored values, when the cache is cleared, then none remain")
    void givenStoredValues_whenEvictAll_thenNoneRemain() {
        flagCache.store(KEY, ON);
        flagCache.store(OTHER_KEY, OFF);

        flagCache.evictAll();

        assertAll(
                () -> assertTrue(flagCache.lookup(KEY).isEmpty()),
                () -> assertTrue(flagCache.lookup(OTHER_KEY).isEmpty())
        );
    }
}
