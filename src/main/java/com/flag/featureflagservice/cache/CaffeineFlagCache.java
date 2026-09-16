package com.flag.featureflagservice.cache;

import com.flag.featureflagservice.evaluation.FlagRules;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

/**
 * In-process {@link FlagCache}. No infrastructure to run, and lookups stay in nanoseconds.
 *
 * <p>The TTL is a safety net rather than the correctness mechanism: writes evict eagerly, so the
 * expiry only matters if this instance missed a write — which, with one instance, it cannot.
 */
@Component
public class CaffeineFlagCache implements FlagCache {

    private final Cache<FlagCacheKey, FlagRules> cache;

    public CaffeineFlagCache(@Value("${app.cache.ttl-seconds:600}") long ttlSeconds,
                             @Value("${app.cache.max-size:10000}") long maxSize) {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(ttlSeconds))
                .maximumSize(maxSize)
                .build();
    }

    @Override
    public Optional<FlagRules> lookup(FlagCacheKey key) {
        return Optional.ofNullable(cache.getIfPresent(key));
    }

    @Override
    public void store(FlagCacheKey key, FlagRules rules) {
        cache.put(key, rules);
    }

    @Override
    public void evict(FlagCacheKey key) {
        cache.invalidate(key);
    }

    @Override
    public void evictAll() {
        cache.invalidateAll();
    }
}
