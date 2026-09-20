package com.strangerthings;

import com.strangerthings.model.Booking;
import com.strangerthings.model.BookingStatus;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BookingServiceTest {

    @Test
    void approvedBookingCanBeMarkedOnLoan() {
        // Arrange
        Booking booking = new Booking(
                1,
                1,
                "Cordless Drill",
                "Test Member",
                LocalDate.now(),
                LocalDate.now().plusDays(3),
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
                2,
                "Ladder",
                "Test Member",
                LocalDate.now(),
                LocalDate.now().plusDays(3),
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
                3,
                "Pressure Washer",
                "Test Member",
                LocalDate.now(),
                LocalDate.now().plusDays(3),
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
                4,
                "Circular Saw",
                "Test Member",
                LocalDate.now(),
                LocalDate.now().plusDays(3),
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