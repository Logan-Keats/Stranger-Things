package com.strangerthings.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.strangerthings.db.DatabaseInitializer;
import com.strangerthings.db.SqliteConnection;

class ReportDaoTest {

    private static Connection anchorConnection;

    @BeforeAll
    static void setUpDatabase() throws SQLException {
        System.setProperty("strangerthings.db.url", "jdbc:sqlite:file:report-dao-test?mode=memory&cache=shared");
        anchorConnection = SqliteConnection.getInstance();
        DatabaseInitializer.initialize();
        try (PreparedStatement statement = anchorConnection.prepareStatement(
                "INSERT INTO items (name, category, status) VALUES (?, ?, ?)")) {
            insert(statement, "Drill", "Tools", "ACTIVE");
            insert(statement, "Ladder", "Tools", "ACTIVE");
            insert(statement, "Mixer", "Kitchen", "INACTIVE");
        }
    }

    @AfterAll
    static void closeDatabase() throws SQLException {
        anchorConnection.close();
        System.clearProperty("strangerthings.db.url");
    }

    @Test
    void readsCountsAndCategoriesFromSqlite() {
        ReportDao dao = new ReportDao();

        assertEquals(3, dao.getTotalSharedCount());
        assertEquals(2, dao.getActiveListingCount());
        assertEquals(2, dao.getCategoryDistribution().get("Tools"));
        assertEquals(1, dao.getCategoryDistribution().get("Kitchen"));
    }

    private static void insert(PreparedStatement statement, String name, String category, String status)
            throws SQLException {
        statement.setString(1, name);
        statement.setString(2, category);
        statement.setString(3, status);
        statement.executeUpdate();
    }
}
