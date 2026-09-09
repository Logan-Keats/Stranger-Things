package com.strangerthings;

/**
 * Login rules with no UI. Stub in-memory accounts (replace with DB later).
 * Failure: returns {@code null}.
 */
public class AuthService {

    /**
     * Attempts login with the given credentials.
     *
     * @param username account name (callers may trim before calling)
     * @param password account password (not trimmed)
     * @return logged-in {@link User}, or {@code null} if credentials are wrong
     */
    public User login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }

        if ("member".equals(username) && "member".equals(password)) {
            return new User(username, Role.MEMBER);
        }
        if ("admin".equals(username) && "admin".equals(password)) {
            return new User(username, Role.ADMIN);
        }

        return null;
    }
}
