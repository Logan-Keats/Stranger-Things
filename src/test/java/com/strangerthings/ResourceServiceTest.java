package com.strangerthings;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceServiceTest {

    @Test
    void validResourceDetailsShouldBeAccepted() {

        ResourceService service = new ResourceService();

        boolean result = service.isValidResource(
                "Drill",
                "Cordless power drill",
                "Tools",
                "Pick Up"
        );

        assertTrue(result);
    }

    @Test
    void missingResourceNameShouldBeRejected() {

        ResourceService service = new ResourceService();

        boolean result = service.isValidResource(
                "",
                "Cordless power drill",
                "Tools",
                "Pick Up"
        );

        assertFalse(result);
    }

    @Test
    void missingDescriptionShouldBeRejected() {
        ResourceService service = new ResourceService();

        boolean result = service.isValidResource(
                "Drill",
                "",
                "Tools",
                "Pick Up"
        );

        assertFalse(result);
    }
    @Test
    void missingCategoryShouldBeRejected() {
        ResourceService service = new ResourceService();

        boolean result = service.isValidResource(
                "Drill",
                "Cordless power drill",
                null,
                "Pick Up"
        );

        assertFalse(result);
    }

    @Test
    void missingCollectionMethodShouldBeRejected() {
        ResourceService service = new ResourceService();

        boolean result = service.isValidResource(
                "Drill",
                "Cordless power drill",
                "Tools",
                null
        );

        assertFalse(result);
    }
}