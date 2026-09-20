package com.strangerthings;

import com.strangerthings.model.Booking;
import com.strangerthings.model.BookingStatus;

public class BookingService {

    public void markOnLoan(Booking booking) {
        if (booking.getStatus() != BookingStatus.APPROVED) {
            throw new IllegalStateException("Booking must be approved before it can be marked on loan.");
        }

        booking.setStatus(BookingStatus.ON_LOAN);
    }

    public void markReturned(Booking booking) {
        if (booking.getStatus() != BookingStatus.ON_LOAN) {
            throw new IllegalStateException("Booking must be on loan before it can be returned.");
        }

        booking.setStatus(BookingStatus.RETURNED);
    }

}