package com.strangerthings.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/** Creates the shared resource table the first time the database is used. */
public final class DatabaseInitializer {

    private static final String CREATE_ITEMS_TABLE = """
            CREATE TABLE IF NOT EXISTS items (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                category TEXT NOT NULL,
                status TEXT NOT NULL
            )
            """;

    private DatabaseInitializer() {
    }

    public static void initialize() throws SQLException {
        try (Connection connection = SqliteConnection.getInstance();
                Statement statement = connection.createStatement()) {
            statement.execute(CREATE_ITEMS_TABLE);
        }
    }
}
