package com.strangerthings;

/** Holds the user authenticated in the current desktop application session. */
public final class UserSession {
    private static User currentUser;

    private UserSession() {
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static String username() {
        return currentUser == null ? "" : currentUser.getUsername();
    }
}
