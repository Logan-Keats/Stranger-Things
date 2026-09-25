package com.strangerthings.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.strangerthings.db.DatabaseInitializer;
import com.strangerthings.db.SqliteConnection;
import com.strangerthings.model.Resource;

public class SqliteResourceDao implements ResourceDao {

    public SqliteResourceDao() {
        try {
            DatabaseInitializer.initialize();
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Unable to initialise the resource database.", exception);
        }
    }

    @Override
    public boolean create(Resource resource) {
        String sql = """
                INSERT INTO items
                (name, category, status, owner_username, description,
                 collection_method, estimated_savings, co2_avoided_kg)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = SqliteConnection.getInstance();
             PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, resource.getName());
            statement.setString(2, resource.getCategory());
            statement.setString(3, resource.getStatus());
            statement.setString(4, resource.getOwnerUsername());
            statement.setString(5, resource.getDescription());
            statement.setString(6, resource.getCollectionMethod());
            statement.setDouble(7, resource.getEstimatedSavings());
            statement.setDouble(8, resource.getCo2AvoidedKg());

            if (statement.executeUpdate() != 1) {
                return false;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    resource.setId(generatedKeys.getInt(1));
                }
            }

            return true;

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Unable to save resource.", exception);
        }
    }

    @Override
    public List<Resource> findAll() {
        List<Resource> resources = new ArrayList<>();

        String sql = "SELECT * FROM items ORDER BY id";

        try (Connection connection = SqliteConnection.getInstance();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                resources.add(mapResource(results));
            }

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Unable to read resources.", exception);
        }

        return resources;
    }

    @Override
    public Resource findById(int resourceId) {
        String sql = "SELECT * FROM items WHERE id = ?";

        try (Connection connection = SqliteConnection.getInstance();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, resourceId);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return mapResource(results);
                }
            }

            return null;

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Unable to read resource.", exception);
        }
    }

    @Override
    public boolean update(Resource resource) {
        String sql = """
                UPDATE items
                SET name = ?,
                    category = ?,
                    status = ?,
                    owner_username = ?,
                    description = ?,
                    collection_method = ?,
                    estimated_savings = ?,
                    co2_avoided_kg = ?
                WHERE id = ?
                """;

        try (Connection connection = SqliteConnection.getInstance();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, resource.getName());
            statement.setString(2, resource.getCategory());
            statement.setString(3, resource.getStatus());
            statement.setString(4, resource.getOwnerUsername());
            statement.setString(5, resource.getDescription());
            statement.setString(6, resource.getCollectionMethod());
            statement.setDouble(7, resource.getEstimatedSavings());
            statement.setDouble(8, resource.getCo2AvoidedKg());
            statement.setInt(9, resource.getId());

            return statement.executeUpdate() == 1;

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Unable to update resource.", exception);
        }
    }

    @Override
    public boolean delete(int resourceId) {
        String sql = "DELETE FROM items WHERE id = ?";

        try (Connection connection = SqliteConnection.getInstance();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, resourceId);

            return statement.executeUpdate() == 1;

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Unable to delete resource.", exception);
        }
    }

    private Resource mapResource(ResultSet results) throws SQLException {
        return new Resource(
                results.getInt("id"),
                results.getString("name"),
                results.getString("category"),
                results.getString("status"),
                results.getString("owner_username"),
                results.getString("description"),
                results.getString("collection_method"),
                results.getDouble("estimated_savings"),
                results.getDouble("co2_avoided_kg")
        );
    }
}