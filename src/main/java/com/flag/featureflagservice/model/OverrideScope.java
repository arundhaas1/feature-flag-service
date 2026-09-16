package com.flag.featureflagservice.model;

/**
 * What an override applies to.
 *
 * <p>Only {@link #ORG} is wired into evaluation today: each data centre runs its own instance,
 * so "enable for this DC" is answered by toggling the flag in that DC's deployment. The column
 * exists anyway so that adding {@code DC} or {@code USER} later is a code change, not a
 * database migration.
 */
public enum OverrideScope {
    ORG,
    DC,
    USER
}
