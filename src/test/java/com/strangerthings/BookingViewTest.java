package com.strangerthings;

import com.strangerthings.model.Booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookingViewTest
{
    private BookingService bookingTest;

    @BeforeEach
    void setup()
    {
        bookingTest = new BookingService();
    }

    @Test
    void getBookingForUser_filterByUsernameSuccessfully()
    {
        bookingTest.requestBooking(1, "Electric Screwdrive", "Nosam", LocalDate.now(),LocalDate.now().plusDays(1));
        bookingTest.requestBooking(2, "Lenovo Thinkpad", "Pascal", LocalDate.now(),LocalDate.now().plusDays(1));

        List<Booking> samBookings = bookingTest.getUserBookings("Nosam");
        assertEquals(1, samBookings.size());
        assertEquals("Nosam", samBookings.get(0).getBorrowerUsername());
    }

    @Test
    void cancelBooking_updateStatus()
    {
        bookingTest.requestBooking(1, "Gel Gun", "TestUser0", LocalDate.now(), LocalDate.now().plusDays(1));
        Booking booking = bookingTest.getUserBookings("TestUser0").get(0);

        boolean cancelled = bookingTest.cancelBooking(booking.getId());

        assertTrue(cancelled);
    }
}