package com.flag.featureflagservice.evaluation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.flag.featureflagservice.TestConstants.ORG_ID;
import static com.flag.featureflagservice.TestConstants.OTHER_ORG_ID;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("FlagRules")
class FlagRulesTest {

    @Test
    @DisplayName("Given no overrides, when resolving, then the environment default decides")
    void givenNoOverrides_whenResolve_thenEnvironmentDefaultDecides() {
        assertAll(
                () -> assertTrue(new FlagRules(true, Map.of()).resolve(ORG_ID)),
                () -> assertFalse(new FlagRules(false, Map.of()).resolve(ORG_ID))
        );
    }

    @Test
    @DisplayName("Given an override for this org, when resolving, then it beats the default")
    void givenOverrideForThisOrg_whenResolve_thenItBeatsTheDefault() {
        assertAll(
                () -> assertTrue(new FlagRules(false, Map.of(ORG_ID, true)).resolve(ORG_ID)),
                () -> assertFalse(new FlagRules(true, Map.of(ORG_ID, false)).resolve(ORG_ID))
        );
    }

    @Test
    @DisplayName("Given an override for a different org, when resolving, then the default still decides")
    void givenOverrideForDifferentOrg_whenResolve_thenDefaultStillDecides() {
        FlagRules rules = new FlagRules(false, Map.of(OTHER_ORG_ID, true));

        assertFalse(rules.resolve(ORG_ID));
    }

    @Test
    @DisplayName("Given no org on the request, when resolving, then overrides are ignored")
    void givenNoOrgOnRequest_whenResolve_thenOverridesAreIgnored() {
        FlagRules rules = new FlagRules(false, Map.of(ORG_ID, true));

        assertFalse(rules.resolve(null));
    }

    @Test
    @DisplayName("Given a flag absent from this environment, when resolving, then it is off")
    void givenFlagAbsentFromEnvironment_whenResolve_thenItIsOff() {
        assertAll(
                () -> assertFalse(FlagRules.absent().resolve(ORG_ID)),
                () -> assertFalse(FlagRules.absent().resolve(null)),
                () -> assertFalse(FlagRules.absent().flagPresent())
        );
    }

    @Test
    @DisplayName("Given a flag present in this environment, when asked, then it reports as present")
    void givenFlagPresentInEnvironment_whenAsked_thenReportsPresent() {
        assertTrue(new FlagRules(false, Map.of()).flagPresent());
    }
}
