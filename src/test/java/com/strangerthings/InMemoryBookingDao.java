package com.strangerthings;

import java.util.ArrayList;
import java.util.List;

import com.strangerthings.dao.BookingDao;
import com.strangerthings.model.Booking;
import com.strangerthings.model.BookingStatus;

/** Test-only booking store that keeps unit tests independent of SQLite data. */
final class InMemoryBookingDao implements BookingDao {
    private final List<Booking> bookings = new ArrayList<>();
    private int nextId = 1;

    @Override
    public boolean create(Booking booking) {
        booking.setId(nextId++);
        bookings.add(booking);
        return true;
    }

    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(bookings);
    }

    @Override
    public List<Booking> findByBorrower(String username) {
        return bookings.stream()
                .filter(booking -> booking.getBorrowerUsername().equalsIgnoreCase(username))
                .toList();
    }

    @Override
    public boolean updateStatus(int bookingId, BookingStatus status) {
        return bookings.stream()
                .filter(booking -> booking.getId() == bookingId)
                .findFirst()
                .map(booking -> {
                    booking.setStatus(status);
                    return true;
                })
                .orElse(false);
    }
}
