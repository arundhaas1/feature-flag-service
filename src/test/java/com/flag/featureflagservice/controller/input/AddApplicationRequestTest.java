package com.flag.featureflagservice.controller.input;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.flag.featureflagservice.TestConstants.APP_DESCRIPTION;
import static com.flag.featureflagservice.TestConstants.DEFAULT_APP;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The entity carries a {@code @Pattern} on its name, but entity constraints never run for an
 * incoming request body — the DTO has to carry its own.
 */
@DisplayName("AddApplicationRequest validation")
class AddApplicationRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("Given a well-formed request, when validated, then there are no violations")
    void givenWellFormedRequest_whenValidated_thenNoViolations() {
        assertTrue(validator.validate(request(DEFAULT_APP, APP_DESCRIPTION)).isEmpty());
    }

    @Test
    @DisplayName("Given a blank name, when validated, then it is rejected")
    void givenBlankName_whenValidated_thenRejected() {
        assertAll(
                () -> assertFalse(validator.validate(request("  ", APP_DESCRIPTION)).isEmpty()),
                () -> assertFalse(validator.validate(request(null, APP_DESCRIPTION)).isEmpty())
        );
    }

    @Test
    @DisplayName("Given a name with punctuation or spaces, when validated, then it is rejected")
    void givenNameWithPunctuation_whenValidated_thenRejected() {
        assertAll(
                () -> assertFalse(validator.validate(request("my app", APP_DESCRIPTION)).isEmpty()),
                () -> assertFalse(validator.validate(request("drop;table", APP_DESCRIPTION)).isEmpty())
        );
    }

    @Test
    @DisplayName("Given an over-long name or description, when validated, then it is rejected")
    void givenOverLongValues_whenValidated_thenRejected() {
        assertAll(
                () -> assertFalse(validator.validate(request("a".repeat(31), APP_DESCRIPTION)).isEmpty()),
                () -> assertFalse(validator.validate(request(DEFAULT_APP, "d".repeat(201))).isEmpty())
        );
    }

    private AddApplicationRequest request(String name, String description) {
        AddApplicationRequest request = new AddApplicationRequest();
        request.setName(name);
        request.setDescription(description);
        return request;
    }
}
