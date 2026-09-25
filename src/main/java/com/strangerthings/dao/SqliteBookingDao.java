package com.strangerthings.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.strangerthings.db.DatabaseInitializer;
import com.strangerthings.db.SqliteConnection;
import com.strangerthings.model.Booking;
import com.strangerthings.model.BookingStatus;

/** SQLite implementation of the booking repository. */
public class SqliteBookingDao implements BookingDao {
    public SqliteBookingDao() {
        try {
            DatabaseInitializer.initialize();
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to initialise the booking database.", exception);
        }
    }

    @Override
    public boolean create(Booking booking) {
        String sql = "INSERT INTO bookings (resource_id, resource_name, borrower_username, start_date, end_date, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = SqliteConnection.getInstance();
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ResourceRecord resource = findResource(connection, booking.getResourceName());
            if (resource == null) {
                return false;
            }
            booking.setResourceId(resource.id());
            booking.setResourceName(resource.name());
            statement.setInt(1, booking.getResourceId());
            statement.setString(2, booking.getResourceName());
            statement.setString(3, booking.getBorrowerUsername());
            statement.setString(4, booking.getStartDate().toString());
            statement.setString(5, booking.getEndDate().toString());
            statement.setString(6, booking.getStatus().name());
            if (statement.executeUpdate() != 1) {
                return false;
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    booking.setId(generatedKeys.getInt(1));
                }
            }
            writeAuditEvent(connection, "Requested", "Booking request", booking.getBorrowerUsername(),
                    booking.getResourceId(), booking.getResourceName());
            return true;
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to save booking.", exception);
        }
    }

    @Override
    public List<Booking> findAll() {
        return query("SELECT * FROM bookings ORDER BY created_at DESC, id DESC", null);
    }

    @Override
    public List<Booking> findByBorrower(String username) {
        return query("SELECT * FROM bookings WHERE LOWER(borrower_username) = LOWER(?) ORDER BY created_at DESC, id DESC", username);
    }

    @Override
    public List<Booking> findByResourceId(int resourceId) {
        return queryByResourceId(
                "SELECT * FROM bookings WHERE resource_id = ? " +
                        "ORDER BY created_at DESC, id DESC",
                resourceId
        );
    }

    @Override
    public boolean updateStatus(int bookingId, BookingStatus status) {
        try (Connection connection = SqliteConnection.getInstance();
                PreparedStatement statement = connection.prepareStatement("UPDATE bookings SET status = ? WHERE id = ?")) {
            BookingRecord booking = findBooking(connection, bookingId);
            if (booking == null) {
                return false;
            }
            statement.setString(1, status.name());
            statement.setInt(2, bookingId);
            if (statement.executeUpdate() != 1) {
                return false;
            }
            String label = statusLabel(status);
            writeAuditEvent(connection, label, "Booking " + label.toLowerCase(), booking.borrowerUsername(),
                    booking.resourceId(), booking.resourceName());
            return true;
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to update booking status.", exception);
        }
    }

    private List<Booking> query(String sql, String username) {
        List<Booking> bookings = new ArrayList<>();
        try (Connection connection = SqliteConnection.getInstance();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            if (username != null) {
                statement.setString(1, username);
            }
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    bookings.add(new Booking(results.getInt("id"), results.getInt("resource_id"),
                            results.getString("resource_name"), results.getString("borrower_username"),
                            LocalDate.parse(results.getString("start_date")), LocalDate.parse(results.getString("end_date")),
                            BookingStatus.valueOf(results.getString("status"))));
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to read bookings.", exception);
        }
        return bookings;
    }

    private List<Booking> queryByResourceId(String sql, int resourceId) {
        List<Booking> bookings = new ArrayList<>();

        try (Connection connection = SqliteConnection.getInstance();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, resourceId);

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    bookings.add(new Booking(
                            results.getInt("id"),
                            results.getInt("resource_id"),
                            results.getString("resource_name"),
                            results.getString("borrower_username"),
                            LocalDate.parse(results.getString("start_date")),
                            LocalDate.parse(results.getString("end_date")),
                            BookingStatus.valueOf(results.getString("status"))
                    ));
                }
            }

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Unable to read resource booking history.",
                    exception
            );
        }

        return bookings;
    }

    private ResourceRecord findResource(Connection connection, String name) throws SQLException {
        String sql = "SELECT id, name FROM items WHERE LOWER(name) = LOWER(?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? new ResourceRecord(results.getInt("id"), results.getString("name")) : null;
            }
        }
    }

    private BookingRecord findBooking(Connection connection, int bookingId) throws SQLException {
        String sql = "SELECT resource_id, resource_name, borrower_username FROM bookings WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);
            try (ResultSet results = statement.executeQuery()) {
                return results.next()
                        ? new BookingRecord(results.getInt("resource_id"), results.getString("resource_name"),
                                results.getString("borrower_username"))
                        : null;
            }
        }
    }

    private void writeAuditEvent(Connection connection, String type, String action, String username,
            int resourceId, String resourceName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO audit_log (event_type, action, username, resource_id, resource_name) VALUES (?, ?, ?, ?, ?)")) {
            statement.setString(1, type);
            statement.setString(2, action);
            statement.setString(3, username);
            statement.setInt(4, resourceId);
            statement.setString(5, resourceName);
            statement.executeUpdate();
        }
    }

    private String statusLabel(BookingStatus status) {
        return switch (status) {
            case APPROVED -> "Approved";
            case REJECTED -> "Rejected";
            case ON_LOAN -> "On loan";
            case RETURNED -> "Returned";
            case CANCELLED -> "Cancelled";
            case REQUESTED -> "Requested";
        };
    }

    private record ResourceRecord(int id, String name) {
    }

    private record BookingRecord(int resourceId, String resourceName, String borrowerUsername) {
    }
}
