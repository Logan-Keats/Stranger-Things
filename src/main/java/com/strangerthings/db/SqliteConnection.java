package com.strangerthings.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Provides JDBC connections to the local neighbourhood SQLite database. */
public final class SqliteConnection {

    private static final String DEFAULT_URL = "jdbc:sqlite:neighbourhood.db";

    private SqliteConnection() {
    }

    public static Connection getInstance() throws SQLException {
        return DriverManager.getConnection(System.getProperty("strangerthings.db.url", DEFAULT_URL));
    }
}
