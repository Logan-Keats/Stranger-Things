package com.strangerthings;

import com.strangerthings.model.Booking;
import com.strangerthings.model.BookingStatus;

import java.util.List;

public class ApprovalService {

    public boolean approveBooking(
            Booking booking,
            List<Booking> existingBookings) {

        // Only REQUESTED bookings can be approved
        if (booking.getStatus() != BookingStatus.REQUESTED) {
            return false;
        }

        // Check existing bookings for conflicts
        for (Booking existing : existingBookings) {

            // Only check bookings for the same resource
            if (existing.getResourceId() != booking.getResourceId()) {
                continue;
            }

            // Only APPROVED and ON_LOAN bookings block approval
            if (existing.getStatus() != BookingStatus.APPROVED
                    && existing.getStatus() != BookingStatus.ON_LOAN) {
                continue;
            }

            // Check if the dates overlap
            boolean overlaps =
                    !booking.getEndDate().isBefore(existing.getStartDate())
                            && !booking.getStartDate().isAfter(existing.getEndDate());

            if (overlaps) {
                return false;
            }
        }

        // No conflict, so approve the booking
        booking.setStatus(BookingStatus.APPROVED);
        return true;
    }

    public boolean rejectBooking(Booking booking) {

        // Only REQUESTED bookings can be rejected
        if (booking.getStatus() != BookingStatus.REQUESTED) {
            return false;
        }

        booking.setStatus(BookingStatus.REJECTED);
        return true;
    }
}