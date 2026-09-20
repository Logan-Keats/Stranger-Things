package com.strangerthings;

import com.strangerthings.model.Booking;
import com.strangerthings.model.BookingStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApprovalServiceTest {

    @Test
    void requestedBookingCanBeApprovedWhenThereIsNoOverlap() {

        Booking booking = new Booking(
                1,
                100,
                "Drill",
                "member1",
                LocalDate.of(2026, 8, 27),
                LocalDate.of(2026, 8, 29),
                BookingStatus.REQUESTED
        );

        ApprovalService service = new ApprovalService();

        boolean result = service.approveBooking(
                booking,
                List.of()
        );

        assertTrue(result);
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }


    @Test
    void overlappingApprovedBookingPreventsApproval() {

        Booking existingBooking = new Booking(
                1,
                100,
                "Drill",
                "member1",
                LocalDate.of(2026, 8, 24),
                LocalDate.of(2026, 8, 26),
                BookingStatus.APPROVED
        );

        Booking newBooking = new Booking(
                2,
                100,
                "Drill",
                "member2",
                LocalDate.of(2026, 8, 26),
                LocalDate.of(2026, 8, 28),
                BookingStatus.REQUESTED
        );

        ApprovalService service = new ApprovalService();

        boolean result = service.approveBooking(
                newBooking,
                List.of(existingBooking)
        );

        assertFalse(result);
        assertEquals(BookingStatus.REQUESTED, newBooking.getStatus());
        assertEquals(BookingStatus.APPROVED, existingBooking.getStatus());
    }


    @Test
    void overlappingOnLoanBookingPreventsApproval() {

        Booking existingBooking = new Booking(
                1,
                100,
                "Drill",
                "member1",
                LocalDate.of(2026, 8, 24),
                LocalDate.of(2026, 8, 26),
                BookingStatus.ON_LOAN
        );

        Booking newBooking = new Booking(
                2,
                100,
                "Drill",
                "member2",
                LocalDate.of(2026, 8, 25),
                LocalDate.of(2026, 8, 28),
                BookingStatus.REQUESTED
        );

        ApprovalService service = new ApprovalService();

        boolean result = service.approveBooking(
                newBooking,
                List.of(existingBooking)
        );

        assertFalse(result);
        assertEquals(BookingStatus.REQUESTED, newBooking.getStatus());
    }


    @Test
    void rejectedBookingDoesNotPreventApproval() {

        Booking existingBooking = new Booking(
                1,
                100,
                "Drill",
                "member1",
                LocalDate.of(2026, 8, 24),
                LocalDate.of(2026, 8, 26),
                BookingStatus.REJECTED
        );

        Booking newBooking = new Booking(
                2,
                100,
                "Drill",
                "member2",
                LocalDate.of(2026, 8, 24),
                LocalDate.of(2026, 8, 26),
                BookingStatus.REQUESTED
        );

        ApprovalService service = new ApprovalService();

        boolean result = service.approveBooking(
                newBooking,
                List.of(existingBooking)
        );

        assertTrue(result);
        assertEquals(BookingStatus.APPROVED, newBooking.getStatus());
    }


    @Test
    void cancelledBookingDoesNotPreventApproval() {

        Booking existingBooking = new Booking(
                1,
                100,
                "Drill",
                "member1",
                LocalDate.of(2026, 8, 24),
                LocalDate.of(2026, 8, 26),
                BookingStatus.CANCELLED
        );

        Booking newBooking = new Booking(
                2,
                100,
                "Drill",
                "member2",
                LocalDate.of(2026, 8, 24),
                LocalDate.of(2026, 8, 26),
                BookingStatus.REQUESTED
        );

        ApprovalService service = new ApprovalService();

        boolean result = service.approveBooking(
                newBooking,
                List.of(existingBooking)
        );

        assertTrue(result);
        assertEquals(BookingStatus.APPROVED, newBooking.getStatus());
    }


    @Test
    void nonOverlappingBookingCanBeApproved() {

        Booking existingBooking = new Booking(
                1,
                100,
                "Drill",
                "member1",
                LocalDate.of(2026, 8, 24),
                LocalDate.of(2026, 8, 26),
                BookingStatus.APPROVED
        );

        Booking newBooking = new Booking(
                2,
                100,
                "Drill",
                "member2",
                LocalDate.of(2026, 8, 27),
                LocalDate.of(2026, 8, 29),
                BookingStatus.REQUESTED
        );

        ApprovalService service = new ApprovalService();

        boolean result = service.approveBooking(
                newBooking,
                List.of(existingBooking)
        );

        assertTrue(result);
        assertEquals(BookingStatus.APPROVED, newBooking.getStatus());
    }


    @Test
    void alreadyApprovedBookingDoesNotChange() {

        Booking booking = new Booking(
                1,
                100,
                "Drill",
                "member1",
                LocalDate.of(2026, 8, 24),
                LocalDate.of(2026, 8, 26),
                BookingStatus.APPROVED
        );

        ApprovalService service = new ApprovalService();

        boolean result = service.approveBooking(
                booking,
                List.of()
        );

        assertFalse(result);
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }


    @Test
    void requestedBookingCanBeRejected() {

        Booking booking = new Booking(
                1,
                100,
                "Drill",
                "member1",
                LocalDate.of(2026, 8, 24),
                LocalDate.of(2026, 8, 26),
                BookingStatus.REQUESTED
        );

        ApprovalService service = new ApprovalService();

        boolean result = service.rejectBooking(booking);

        assertTrue(result);
        assertEquals(BookingStatus.REJECTED, booking.getStatus());
    }


    @Test
    void approvedBookingCannotBeRejected() {

        Booking booking = new Booking(
                1,
                100,
                "Drill",
                "member1",
                LocalDate.of(2026, 8, 24),
                LocalDate.of(2026, 8, 26),
                BookingStatus.APPROVED
        );

        ApprovalService service = new ApprovalService();

        boolean result = service.rejectBooking(booking);

        assertFalse(result);
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }


    @Test
    void onLoanBookingCannotBeRejected() {

        Booking booking = new Booking(
                1,
                100,
                "Drill",
                "member1",
                LocalDate.of(2026, 8, 24),
                LocalDate.of(2026, 8, 26),
                BookingStatus.ON_LOAN
        );

        ApprovalService service = new ApprovalService();

        boolean result = service.rejectBooking(booking);

        assertFalse(result);
        assertEquals(BookingStatus.ON_LOAN, booking.getStatus());
    }
}