package com.strangerthings.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import com.strangerthings.db.DatabaseInitializer;
import com.strangerthings.db.SqliteConnection;

/** Read-only item statistics used by the Home and Reports dashboards. */
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

    public int getActiveListingCount() {
        return queryCount("SELECT COUNT(*) FROM items WHERE UPPER(status) = 'ACTIVE'");
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

    private int queryCount(String sql) {
        try (Connection connection = SqliteConnection.getInstance();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet results = statement.executeQuery()) {
            return results.next() ? results.getInt(1) : 0;
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to read item statistics.", exception);
        }
    }
}
