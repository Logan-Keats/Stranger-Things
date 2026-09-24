package com.strangerthings;

import java.time.LocalDate;
import java.util.List;

import com.strangerthings.dao.BookingDao;
import com.strangerthings.dao.SqliteBookingDao;
import com.strangerthings.model.Booking;
import com.strangerthings.model.BookingStatus;

/** Booking workflow backed by the shared SQLite database. */
public class BookingService {
    private static BookingService instance;
    private final BookingDao bookingDao;

    public BookingService() {
        this(new SqliteBookingDao());
    }

    BookingService(BookingDao bookingDao) {
        this.bookingDao = bookingDao;
    }

    public static synchronized BookingService getInstance() {
        if (instance == null) {
            instance = new BookingService();
        }
        return instance;
    }

    public boolean requestBooking(int resourceId, String resourceName, String username, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || endDate.isBefore(startDate)
                || username == null || username.isBlank() || resourceName == null || resourceName.isBlank()) {
            return false;
        }
        return bookingDao.create(new Booking(resourceId, resourceName, username, startDate, endDate));
    }

    public List<Booking> getUserBookings(String username) {
        return bookingDao.findByBorrower(username);
    }

    public List<Booking> getAllBookings() {
        return bookingDao.findAll();
    }

    public boolean cancelBooking(int bookingId) {
        boolean isRequested = bookingDao.findAll().stream()
                .anyMatch(booking -> booking.getId() == bookingId && booking.getStatus() == BookingStatus.REQUESTED);
        if (!isRequested) {
            return false;
        }
        return bookingDao.updateStatus(bookingId, BookingStatus.CANCELLED);
    }

    public void markOnLoan(Booking booking) {
        if (booking.getStatus() != BookingStatus.APPROVED) {
            throw new IllegalStateException(
                    "Booking must be approved before it can be marked on loan."
            );
        }

        boolean updated = bookingDao.updateStatus(
                booking.getId(),
                BookingStatus.ON_LOAN
        );

        if (!updated) {
            throw new IllegalStateException(
                    "Failed to update booking status."
            );
        }

        booking.setStatus(BookingStatus.ON_LOAN);
    }

    public void markReturned(Booking booking) {
        if (booking.getStatus() != BookingStatus.ON_LOAN) {
            throw new IllegalStateException(
                    "Booking must be on loan before it can be returned."
            );
        }

        boolean updated = bookingDao.updateStatus(
                booking.getId(),
                BookingStatus.RETURNED
        );

        if (!updated) {
            throw new IllegalStateException(
                    "Failed to update booking status."
            );
        }

        booking.setStatus(BookingStatus.RETURNED);
    }

    public boolean saveStatus(Booking booking) {
        return booking != null && bookingDao.updateStatus(booking.getId(), booking.getStatus());
    }
}
