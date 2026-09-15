package com.flag.featureflagservice.cache;

import java.util.Optional;

/**
 * Stores evaluation answers so the hot read path can skip the database.
 *
 * <p>The interface exists so the in-process implementation can be swapped for a shared one
 * (Redis) without the service noticing. That swap is the point at which a second instance of
 * this application stops being able to serve stale answers after another instance's write.
 */
public interface FlagCache {

    /** Empty means "not cached" — never "cached as false". */
    Optional<Boolean> lookup(FlagCacheKey key);

    void store(FlagCacheKey key, boolean enabled);

    void evict(FlagCacheKey key);

    void evictAll();
}
