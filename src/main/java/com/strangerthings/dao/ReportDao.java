package com.strangerthings.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.strangerthings.db.DatabaseInitializer;
import com.strangerthings.db.SqliteConnection;

/** SQL queries that provide Home and Reports with live application statistics. */
public class ReportDao {
    public ReportDao() {
        try {
            DatabaseInitializer.initialize();
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to initialise the SQLite database.", exception);
        }
    }

    public int getTotalSharedCount() {
        return queryCount("SELECT COUNT(*) FROM items");
    }

    public int getTotalSharedCount(String ownerUsername) {
        return queryCount("SELECT COUNT(*) FROM items WHERE LOWER(owner_username) = LOWER(?)", ownerUsername);
    }

    public int getActiveListingCount() {
        return queryCount("SELECT COUNT(*) FROM items WHERE UPPER(status) = 'ACTIVE'");
    }

    public int getTotalBorrowingCount() {
        return queryCount("SELECT COUNT(*) FROM bookings");
    }

    public int getTotalBorrowingCount(String borrowerUsername) {
        return queryCount("SELECT COUNT(*) FROM bookings WHERE LOWER(borrower_username) = LOWER(?)", borrowerUsername);
    }

    public int getCurrentBorrowingCount() {
        return queryCount("SELECT COUNT(*) FROM bookings WHERE UPPER(status) IN ('APPROVED', 'ON_LOAN')");
    }

    public double getTotalSavings() {
        return queryDecimal("SELECT COALESCE(SUM(estimated_savings), 0) FROM items");
    }

    public double getTotalSavings(String ownerUsername) {
        return queryDecimal("SELECT COALESCE(SUM(estimated_savings), 0) FROM items WHERE LOWER(owner_username) = LOWER(?)", ownerUsername);
    }

    public double getCo2AvoidedKg() {
        return queryDecimal("SELECT COALESCE(SUM(co2_avoided_kg), 0) FROM items");
    }

    public double getCo2AvoidedKg(String ownerUsername) {
        return queryDecimal("SELECT COALESCE(SUM(co2_avoided_kg), 0) FROM items WHERE LOWER(owner_username) = LOWER(?)", ownerUsername);
    }

    public Map<String, Integer> getCategoryDistribution() {
        String sql = "SELECT category, COUNT(*) AS item_count FROM items GROUP BY category ORDER BY category";
        Map<String, Integer> distribution = new LinkedHashMap<>();
        try (Connection connection = SqliteConnection.getInstance();
                Statement statement = connection.createStatement();
                ResultSet results = statement.executeQuery(sql)) {
            while (results.next()) {
                distribution.put(results.getString("category"), results.getInt("item_count"));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to read item categories.", exception);
        }
        return distribution;
    }

    public Map<String, Integer> getCategoryDistribution(String ownerUsername) {
        String sql = "SELECT category, COUNT(*) AS item_count FROM items WHERE LOWER(owner_username) = LOWER(?) GROUP BY category ORDER BY category";
        Map<String, Integer> distribution = new LinkedHashMap<>();
        try (Connection connection = SqliteConnection.getInstance();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, ownerUsername);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    distribution.put(results.getString("category"), results.getInt("item_count"));
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to read user item categories.", exception);
        }
        return distribution;
    }

    public Map<String, Integer> getBorrowingActivityByDay() {
        String sql = "SELECT substr(created_at, 1, 10) AS day, COUNT(*) AS booking_count "
                + "FROM bookings GROUP BY substr(created_at, 1, 10) ORDER BY day";
        Map<String, Integer> activity = new LinkedHashMap<>();
        try (Connection connection = SqliteConnection.getInstance();
                Statement statement = connection.createStatement();
                ResultSet results = statement.executeQuery(sql)) {
            while (results.next()) {
                activity.put(results.getString("day"), results.getInt("booking_count"));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to read borrowing activity.", exception);
        }
        return activity;
    }

    public List<AuditRecord> getAuditEvents() {
        return getAuditEvents(null);
    }

    public List<AuditRecord> getAuditEvents(String username) {
        List<AuditRecord> events = new ArrayList<>();
        String sql = "SELECT event_type, action, username, resource_name, occurred_at FROM audit_log "
                + (username == null || username.isBlank() ? "" : "WHERE LOWER(username) = LOWER(?) ")
                + "ORDER BY occurred_at DESC, id DESC";
        try (Connection connection = SqliteConnection.getInstance();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            if (username != null && !username.isBlank()) {
                statement.setString(1, username);
            }
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    events.add(new AuditRecord(results.getString("event_type"), results.getString("action"),
                            results.getString("username"), results.getString("resource_name"),
                            results.getString("occurred_at")));
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to read the audit log.", exception);
        }
        return events;
    }

    private int queryCount(String sql) {
        return queryCount(sql, null);
    }

    private int queryCount(String sql, String parameter) {
        try (Connection connection = SqliteConnection.getInstance();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            if (parameter != null) {
                statement.setString(1, parameter);
            }
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? results.getInt(1) : 0;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to read item statistics.", exception);
        }
    }

    private double queryDecimal(String sql) {
        return queryDecimal(sql, null);
    }

    private double queryDecimal(String sql, String parameter) {
        try (Connection connection = SqliteConnection.getInstance();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            if (parameter != null) {
                statement.setString(1, parameter);
            }
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? results.getDouble(1) : 0;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to read report totals.", exception);
        }
    }

    public record AuditRecord(String type, String action, String username, String resourceName, String occurredAt) {
    }
}
