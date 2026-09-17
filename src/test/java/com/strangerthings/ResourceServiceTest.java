package com.strangerthings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResourceServiceTest {

    private ResourceService resourceService;

    @BeforeEach
    void setUp() {
        resourceService = new ResourceService();
    }

    @Test
    void validResourceDetailsShouldBeAccepted() {
        boolean result = resourceService.isValidResource(
                "Drill",
                "Cordless power drill",
                "Tools",
                "Pick Up"
        );

        assertTrue(result);
    }

    @Test
    void missingResourceNameShouldBeRejected() {
        boolean result = resourceService.isValidResource(
                "",
                "Cordless power drill",
                "Tools",
                "Pick Up"
        );

        assertFalse(result);
    }

    @Test
    void missingDescriptionShouldBeRejected() {
        boolean result = resourceService.isValidResource(
                "Drill",
                "",
                "Tools",
                "Pick Up"
        );

        assertFalse(result);
    }

    @Test
    void missingCategoryShouldBeRejected() {
        boolean result = resourceService.isValidResource(
                "Drill",
                "Cordless power drill",
                null,
                "Pick Up"
        );

        assertFalse(result);
    }

    @Test
    void missingCollectionMethodShouldBeRejected() {
        boolean result = resourceService.isValidResource(
                "Drill",
                "Cordless power drill",
                "Tools",
                null
        );

        assertFalse(result);
    }
}