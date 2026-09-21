package com.strangerthings.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.strangerthings.db.DatabaseInitializer;
import com.strangerthings.db.PasswordHasher;
import com.strangerthings.db.SqliteConnection;

/** User, ownership, and activity queries for Home and Reports. */
public class UserDao {
    public UserDao() {
        try {
            DatabaseInitializer.initialize();
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to initialise the user database.", exception);
        }
    }

    public int getMemberCount() {
        return getUserCount("WHERE UPPER(role) = 'MEMBER'");
    }

    public boolean register(String username, String password, String role) {
        if (username == null || username.isBlank() || password == null || password.length() < 4) {
            return false;
        }
        if (!"MEMBER".equals(role) && !"ADMIN".equals(role)) {
            return false;
        }
        String sql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";
        try (Connection connection = SqliteConnection.getInstance();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username.trim());
            statement.setString(2, PasswordHasher.hash(password));
            statement.setString(3, role);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            if (exception.getMessage() != null && exception.getMessage().toLowerCase().contains("unique")) {
                return false;
            }
            throw new IllegalStateException("Unable to register user.", exception);
        }
    }

    public UserRecord findByCredentials(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        String sql = "SELECT username, role, created_at FROM users WHERE username = ? AND password_hash = ?";
        try (Connection connection = SqliteConnection.getInstance();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username.trim());
            statement.setString(2, PasswordHasher.hash(password));
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? new UserRecord(results.getString("username"), results.getString("role"),
                        results.getString("created_at")) : null;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to authenticate user.", exception);
        }
    }

    public int getUserCount() {
        return getUserCount("");
    }

    public List<UserRecord> findAllMembers() {
        return findUsers("WHERE UPPER(role) = 'MEMBER'");
    }

    public List<UserRecord> findAllUsers() {
        return findUsers("");
    }

    public List<UserActivityRecord> findUserActivities() {
        String sql = """
                SELECT u.username, u.role,
                       COUNT(DISTINCT i.id) AS resource_count,
                       COUNT(DISTINCT b.id) AS booking_count,
                       COUNT(DISTINCT a.id) AS activity_count
                FROM users u
                LEFT JOIN items i ON LOWER(i.owner_username) = LOWER(u.username)
                LEFT JOIN bookings b ON LOWER(b.borrower_username) = LOWER(u.username)
                LEFT JOIN audit_log a ON LOWER(a.username) = LOWER(u.username)
                GROUP BY u.id, u.username, u.role
                ORDER BY CASE WHEN UPPER(u.role) = 'ADMIN' THEN 0 ELSE 1 END, u.username
                """;
        List<UserActivityRecord> activities = new ArrayList<>();
        try (Connection connection = SqliteConnection.getInstance();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                activities.add(new UserActivityRecord(results.getString("username"), results.getString("role"),
                        results.getInt("resource_count"), results.getInt("booking_count"),
                        results.getInt("activity_count")));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to read user activity.", exception);
        }
        return activities;
    }

    private int getUserCount(String clause) {
        try (Connection connection = SqliteConnection.getInstance();
                PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM users " + clause);
                ResultSet results = statement.executeQuery()) {
            return results.next() ? results.getInt(1) : 0;
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to count users.", exception);
        }
    }

    private List<UserRecord> findUsers(String clause) {
        List<UserRecord> users = new ArrayList<>();
        String sql = "SELECT username, role, created_at FROM users " + clause + " ORDER BY created_at DESC, username";
        try (Connection connection = SqliteConnection.getInstance();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                users.add(new UserRecord(results.getString("username"), results.getString("role"),
                        results.getString("created_at")));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to read users.", exception);
        }
        return users;
    }

    public record UserRecord(String username, String role, String createdAt) {
    }

    public record UserActivityRecord(String username, String role, int resourceCount, int bookingCount,
            int activityCount) {
    }
}
