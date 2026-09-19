package com.strangerthings;

import com.strangerthings.model.Booking;
import com.strangerthings.model.BookingStatus;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookingAddTest
{
    private BookingService bookingTest;

    @BeforeEach
    void setup()
    {
        bookingTest = new BookingService();
    }

    @Test
    void createBookingRequest_validData_returnsTrueAndSaves()
    {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(3);

        boolean result = bookingTest.requestBooking(304, "Vacuum", "TestUser0", start, end);

        assertTrue(result);
        List<Booking> userBookings = bookingTest.getUserBookings("TestUser0");
        assertEquals(1, userBookings.size());
        assertEquals("Vacuum", userBookings.get(0).getResourceName());
        assertEquals(BookingStatus.REQUESTED, userBookings.get(0).getStatus());
    }

    @Test
    void createBookingRequest_endDateBeforeStartDate_returnsFalse()
    {
        LocalDate start = LocalDate.now().plusDays(5);
        LocalDate end = LocalDate.now().plusDays(2);

        boolean result = bookingTest.requestBooking(403, "Lawn Mower", "Tom", start, end);

        assertFalse(result);
    }
}