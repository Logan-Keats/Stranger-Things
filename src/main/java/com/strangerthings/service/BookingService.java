package com.strangerthings.service;

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

    public BookingService(BookingDao bookingDao) {
        this.bookingDao = bookingDao;
    }

    public static synchronized BookingService getInstance() {
        if (instance == null) {
            instance = new BookingService();
        }
        return instance;
    }

    public boolean requestBooking(int resourceId, String resourceName, String username,
                                  LocalDate startDate, LocalDate endDate) {

        // Validate booking details
        if (resourceId <= 0 || startDate == null || endDate == null
                || endDate.isBefore(startDate)
                || username == null || username.isBlank()
                || resourceName == null || resourceName.isBlank()) {
            return false;
        }

        // Check existing bookings for this resource
        List<Booking> existingBookings = bookingDao.findByResourceId(resourceId);

        for (Booking existing : existingBookings) {

            // Only active bookings block new requests
            BookingStatus status = existing.getStatus();

            if (status == BookingStatus.REQUESTED
                    || status == BookingStatus.APPROVED
                    || status == BookingStatus.ON_LOAN) {

                // Check whether the dates overlap
                boolean overlaps =
                        !startDate.isAfter(existing.getEndDate())
                                && !endDate.isBefore(existing.getStartDate());

                if (overlaps) {
                    return false;
                }
            }
        }

        // No overlapping bookings found
        return bookingDao.create(
                new Booking(resourceId, resourceName, username, startDate, endDate)
        );
    }

    public List<Booking> getUserBookings(String username) {
        return bookingDao.findByBorrower(username);
    }

    /** Returns bookings that still need action or are currently on loan. */
    public List<Booking> getActiveUserBookings(String username) {
        return getUserBookings(username).stream()
                .filter(booking -> booking.getStatus() == BookingStatus.REQUESTED
                        || booking.getStatus() == BookingStatus.APPROVED
                        || booking.getStatus() == BookingStatus.ON_LOAN)
                .toList();
    }

    public List<Booking> getAllBookings() {
        return bookingDao.findAll();
    }

    public List<Booking> getResourceHistory(int resourceId) {
        return bookingDao.findByResourceId(resourceId);
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
        transitionStatus(
                booking,
                BookingStatus.APPROVED,
                BookingStatus.ON_LOAN,
                "Booking must be approved before it can be marked on loan."
        );
    }

    public void markReturned(Booking booking) {
        transitionStatus(
                booking,
                BookingStatus.ON_LOAN,
                BookingStatus.RETURNED,
                "Booking must be on loan before it can be returned."
        );
    }

    private void transitionStatus(
            Booking booking,
            BookingStatus requiredStatus,
            BookingStatus newStatus,
            String invalidTransitionMessage) {

        if (booking.getStatus() != requiredStatus) {
            throw new IllegalStateException(invalidTransitionMessage);
        }

        boolean updated = bookingDao.updateStatus(
                booking.getId(),
                newStatus
        );

        if (!updated) {
            throw new IllegalStateException(
                    "Failed to update booking status."
            );
        }

        booking.setStatus(newStatus);
    }

    public boolean saveStatus(Booking booking) {
        return booking != null && bookingDao.updateStatus(booking.getId(), booking.getStatus());
    }

    public boolean isResourceAvailable(int resourceId) {
        return bookingDao.findByResourceId(resourceId).stream()
                .noneMatch(booking ->
                        booking.getStatus() == BookingStatus.ON_LOAN);
    }

}
