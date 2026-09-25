package com.strangerthings.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** Creates and upgrades the local SQLite schema used by bookings and reports. */
public final class DatabaseInitializer {

    private DatabaseInitializer() {
    }

    public static void initialize() throws SQLException {
        try (Connection connection = SqliteConnection.getInstance();
                Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT NOT NULL UNIQUE,
                        password_hash TEXT NOT NULL DEFAULT '',
                        role TEXT NOT NULL DEFAULT 'MEMBER',
                        created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
                    )
                    """);
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS items (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        category TEXT NOT NULL,
                        status TEXT NOT NULL,
                        owner_username TEXT NOT NULL DEFAULT '',
                        description TEXT NOT NULL DEFAULT '',
                        collection_method TEXT NOT NULL DEFAULT '',
                        estimated_savings REAL NOT NULL DEFAULT 0,
                        co2_avoided_kg REAL NOT NULL DEFAULT 0
                    )
                    """);
            ensureColumn(connection, "items", "estimated_savings",
                    "REAL NOT NULL DEFAULT 0");

            ensureColumn(connection, "items", "co2_avoided_kg",
                    "REAL NOT NULL DEFAULT 0");

            ensureColumn(connection, "items", "owner_username",
                    "TEXT NOT NULL DEFAULT ''");

            ensureColumn(connection, "items", "description",
                    "TEXT NOT NULL DEFAULT ''");

            ensureColumn(connection, "items", "collection_method",
                    "TEXT NOT NULL DEFAULT ''");
            try (PreparedStatement update = connection.prepareStatement("""
        UPDATE items
        SET description = ?, collection_method = ?
        WHERE name = ? AND (description = '' OR collection_method = '')
        """)) {

                update.setString(1,
                        "Cordless power drill suitable for basic household repairs and DIY projects.");
                update.setString(2, "Pick Up");
                update.setString(3, "Cordless Drill");
                update.executeUpdate();

                update.setString(1,
                        "Electric lawn mower suitable for small to medium-sized lawns.");
                update.setString(2, "Pick Up");
                update.setString(3, "Lawn Mower");
                update.executeUpdate();

                update.setString(1,
                        "3D printer available for small personal projects and prototype printing.");
                update.setString(2, "Pick Up");
                update.setString(3, "3D Printer");
                update.executeUpdate();
            }
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS bookings (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        resource_id INTEGER NOT NULL,
                        resource_name TEXT NOT NULL,
                        borrower_username TEXT NOT NULL,
                        start_date TEXT NOT NULL,
                        end_date TEXT NOT NULL,
                        status TEXT NOT NULL,
                        created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
                    )
                    """);
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS audit_log (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        event_type TEXT NOT NULL,
                        action TEXT NOT NULL,
                        username TEXT NOT NULL,
                        resource_id INTEGER,
                        resource_name TEXT,
                        occurred_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
                    )
            """);
            ensureColumn(connection, "users", "password_hash", "TEXT NOT NULL DEFAULT ''");
            ensureColumn(connection, "audit_log", "resource_id", "INTEGER");
            ensureColumn(connection, "audit_log", "resource_name", "TEXT");
        }
        if (Boolean.parseBoolean(System.getProperty("strangerthings.db.seed", "true"))) {
            seedDemoData();
        }
    }

    private static void ensureColumn(Connection connection, String table, String column, String definition)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("PRAGMA table_info(" + table + ")");
                ResultSet columns = statement.executeQuery()) {
            while (columns.next()) {
                if (column.equalsIgnoreCase(columns.getString("name"))) {
                    return;
                }
            }
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
        }
    }

    private static void seedDemoData() throws SQLException {
        try (Connection connection = SqliteConnection.getInstance()) {
            if (hasLegacyDemoCatalogue(connection)) {
                clearLegacyDemoData(connection);
            }
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT OR IGNORE INTO users (username, password_hash, role, created_at) VALUES (?, ?, ?, ?)")) {
                insertUser(statement, "admin", "admin", "ADMIN", "2026-08-20 09:00:00");
                insertUser(statement, "member", "member", "MEMBER", "2026-08-21 09:00:00");
                insertUser(statement, "Sam", "sam", "MEMBER", "2026-08-22 09:00:00");
                insertUser(statement, "Alex", "alex", "MEMBER", "2026-08-23 09:00:00");
                insertUser(statement, "David", "david", "MEMBER", "2026-08-24 09:00:00");
                insertUser(statement, "Sarah", "sarah", "MEMBER", "2026-08-25 09:00:00");
                insertUser(statement, "Michael", "michael", "MEMBER", "2026-08-26 09:00:00");
            }
            updateBuiltInPassword(connection, "admin", "admin");
            updateBuiltInPassword(connection, "member", "member");
            updateBuiltInPassword(connection, "Sam", "sam");
            updateBuiltInPassword(connection, "Alex", "alex");
            updateBuiltInPassword(connection, "David", "david");
            updateBuiltInPassword(connection, "Sarah", "sarah");
            updateBuiltInPassword(connection, "Michael", "michael");
            if (count(connection, "items") == 0) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO items (name, category, status, owner_username, description, collection_method, estimated_savings, co2_avoided_kg) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                    insertItem(
                            statement,
                            "Cordless Drill",
                            "Tools",
                            "ACTIVE",
                            "David",
                            "Cordless power drill suitable for basic household repairs and DIY projects.",
                            "Pick Up",
                            45.00,
                            5.0
                    );

                    insertItem(
                            statement,
                            "Lawn Mower",
                            "Garden Equipment",
                            "ACTIVE",
                            "Sarah",
                            "Electric lawn mower suitable for small to medium-sized lawns.",
                            "Pick Up",
                            32.50,
                            4.2
                    );

                    insertItem(
                            statement,
                            "3D Printer",
                            "Electronics",
                            "ACTIVE",
                            "Michael",
                            "3D printer available for small personal projects and prototype printing.",
                            "Pick Up",
                            55.00,
                            9.3
                    );
                }
            }
            if (count(connection, "bookings") == 0) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO bookings (resource_id, resource_name, borrower_username, start_date, end_date, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                    insertBooking(statement, 1, "Cordless Drill", "Sam", "2026-08-24", "2026-08-25", "RETURNED", "2026-08-24 09:10:00");
                    insertBooking(statement, 2, "Lawn Mower", "member", "2026-08-25", "2026-08-27", "ON_LOAN", "2026-08-25 10:20:00");
                    insertBooking(statement, 3, "3D Printer", "Alex", "2026-08-27", "2026-08-29", "APPROVED", "2026-08-27 17:10:00");
                }
            }
            if (count(connection, "audit_log") == 0) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO audit_log (event_type, action, username, resource_id, resource_name, occurred_at) VALUES (?, ?, ?, ?, ?, ?)")) {
                    insertAuditEvent(statement, "Requested", "Booking request", "Sam", 1, "Cordless Drill", "2026-08-24 09:10:00");
                    insertAuditEvent(statement, "Returned", "Booking returned", "Sam", 1, "Cordless Drill", "2026-08-25 16:30:00");
                    insertAuditEvent(statement, "Requested", "Booking request", "member", 2, "Lawn Mower", "2026-08-25 10:20:00");
                    insertAuditEvent(statement, "Approved", "Booking approved", "admin", 2, "Lawn Mower", "2026-08-25 10:40:00");
                    insertAuditEvent(statement, "Requested", "Booking request", "Alex", 3, "3D Printer", "2026-08-27 17:10:00");
                }
            }
        }
    }

    private static int count(Connection connection, String table) throws SQLException {
        try (Statement statement = connection.createStatement();
                ResultSet results = statement.executeQuery("SELECT COUNT(*) FROM " + table)) {
            return results.next() ? results.getInt(1) : 0;
        }
    }

    private static boolean hasLegacyDemoCatalogue(Connection connection) throws SQLException {
        String sql = """
                SELECT COUNT(*) = 6
                    AND COUNT(CASE WHEN LOWER(name) IN
                        ('cordless drill', 'extension ladder', 'projector', 'stand mixer', 'slow cooker', 'camping tent')
                        THEN 1 END) = 6
                FROM items
                """;
        try (Statement statement = connection.createStatement(); ResultSet results = statement.executeQuery(sql)) {
            return results.next() && results.getBoolean(1);
        }
    }

    private static void clearLegacyDemoData(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM audit_log");
            statement.executeUpdate("DELETE FROM bookings");
            statement.executeUpdate("DELETE FROM items");
        }
    }

    private static void insertUser(PreparedStatement statement, String username, String password, String role, String createdAt)
            throws SQLException {
        statement.setString(1, username);
        statement.setString(2, PasswordHasher.hash(password));
        statement.setString(3, role);
        statement.setString(4, createdAt);
        statement.executeUpdate();
    }

    private static void updateBuiltInPassword(Connection connection, String username, String password) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE users SET password_hash = ? WHERE username = ? AND password_hash = ''")) {
            statement.setString(1, PasswordHasher.hash(password));
            statement.setString(2, username);
            statement.executeUpdate();
        }
    }

    private static void insertItem(
            PreparedStatement statement,
            String name,
            String category,
            String status,
            String owner,
            String description,
            String collectionMethod,
            double savings,
            double co2) throws SQLException {

        statement.setString(1, name);
        statement.setString(2, category);
        statement.setString(3, status);
        statement.setString(4, owner);
        statement.setString(5, description);
        statement.setString(6, collectionMethod);
        statement.setDouble(7, savings);
        statement.setDouble(8, co2);

        statement.executeUpdate();
    }

    private static void insertBooking(PreparedStatement statement, int resourceId, String resourceName,
            String borrower, String startDate, String endDate, String status, String createdAt) throws SQLException {
        statement.setInt(1, resourceId);
        statement.setString(2, resourceName);
        statement.setString(3, borrower);
        statement.setString(4, startDate);
        statement.setString(5, endDate);
        statement.setString(6, status);
        statement.setString(7, createdAt);
        statement.executeUpdate();
    }

    private static void insertAuditEvent(PreparedStatement statement, String type, String action, String username,
            int resourceId, String resourceName, String occurredAt) throws SQLException {
        statement.setString(1, type);
        statement.setString(2, action);
        statement.setString(3, username);
        statement.setInt(4, resourceId);
        statement.setString(5, resourceName);
        statement.setString(6, occurredAt);
        statement.executeUpdate();
    }
}
