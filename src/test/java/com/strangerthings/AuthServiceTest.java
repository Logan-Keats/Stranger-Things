package com.strangerthings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link AuthService} (JUnit 5).
 * Checks login rules without the UI. D-3 = Red; D-4 = Green.
 */
public class AuthServiceTest {

    /** Service under test; created fresh for each test method. */
    private AuthService authService;

    /**
     * Runs before every {@code @Test}.
     * Creates a new AuthService so tests do not share state.
     */
    @BeforeEach
    void setUp() {
        authService = new AuthService();
    }

    /**
     * US-1.1: valid member credentials return a User with role MEMBER.
     * Fails (Red) while {@link AuthService#login} still returns null.
     */
    @Test
    void login_withValidMember_returnsMemberUser() {
        User user = authService.login("member", "member");

        assertNotNull(user);
        assertEquals("member", user.getUsername());
        assertEquals(Role.MEMBER, user.getRole());
    }

    /**
     * Valid admin credentials return a User with role ADMIN.
     * Distinguishes ADMIN from MEMBER for later role-based UI (US-1.4).
     */
    @Test
    void login_withValidAdmin_returnsAdminUser() {
        User user = authService.login("admin", "admin");

        assertNotNull(user);
        assertEquals("admin", user.getUsername());
        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    void login_withSeededAlexCredentials_returnsMemberUser() {
        User user = authService.login("Alex", "alex");

        assertNotNull(user);
        assertEquals("Alex", user.getUsername());
        assertEquals(Role.MEMBER, user.getRole());
    }

    /**
     * US-1.3: known username with wrong password fails.
     * Stub convention: return null on failure.
     */
    @Test
    void login_withWrongPassword_returnsNull() {
        assertNull(authService.login("member", "wrong"));
    }

    /**
     * US-1.3: unknown username fails (returns null).
     */
    @Test
    void login_withUnknownUser_returnsNull() {
        assertNull(authService.login("nobody", "x"));
    }
}
