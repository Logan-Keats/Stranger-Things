package com.strangerthings;

/**
 * Authenticated user returned by {@link AuthService#login}.
 */
public class User {

    /** Login name shown in the shell header. */
    private final String username;

    /** MEMBER or ADMIN — drives which screens are allowed later. */
    private final Role role;

    public User(String username, Role role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }
}
