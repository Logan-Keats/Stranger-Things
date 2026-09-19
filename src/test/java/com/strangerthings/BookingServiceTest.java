package com.strangerthings;

import com.strangerthings.model.Booking;
import com.strangerthings.model.BookingStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BookingServiceTest {

    @Test
    void approvedBookingCanBeMarkedOnLoan() {
        // Arrange
        Booking booking = new Booking(
                1,
                "Cordless Drill",
                "Test Member",
                BookingStatus.APPROVED
        );

        BookingService service = new BookingService();

        // Act
        service.markOnLoan(booking);

        // Assert
        assertEquals(BookingStatus.ON_LOAN, booking.getStatus());
    }

    @Test
    void onLoanBookingCanBeMarkedReturned() {
        // Arrange
        Booking booking = new Booking(
                2,
                "Ladder",
                "Test Member",
                BookingStatus.ON_LOAN
        );

        BookingService service = new BookingService();

        // Act
        service.markReturned(booking);

        // Assert
        assertEquals(BookingStatus.RETURNED, booking.getStatus());
    }

    @Test
    void requestedBookingCannotBeMarkedReturned() {
        // Arrange
        Booking booking = new Booking(
                3,
                "Pressure Washer",
                "Test Member",
                BookingStatus.REQUESTED
        );

        BookingService service = new BookingService();

        // Act & Assert
        assertThrows(
                IllegalStateException.class,
                () -> service.markReturned(booking)
        );
    }

    @Test
    void requestedBookingCannotBeMarkedOnLoan() {
        // Arrange
        Booking booking = new Booking(
                4,
                "Circular Saw",
                "Test Member",
                BookingStatus.REQUESTED
        );

        BookingService service = new BookingService();

        // Act & Assert
        assertThrows(
                IllegalStateException.class,
                () -> service.markOnLoan(booking)
        );
    }

}