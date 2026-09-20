package com.strangerthings;

import com.strangerthings.model.Booking;
import com.strangerthings.model.BookingStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class BookingService
{
    /*
    Contains:
    - ArrayList (for prototype, to be refactored for sqlite)
    - Methods
     */

    private static BookingService instance;
    // For Functional Prototype, will configure for sqlite in final
    private final List<Booking> arrayBookings = new ArrayList<>();
    private int idCount = 1;

    public BookingService()
    {
        // Mock Data Method For Testing
        seedMockData();
    }

    public static synchronized BookingService getInstance()
    {
        if (instance == null)
        {
            instance = new BookingService();
        }
        return instance;
    }

    // Test Method
    private void seedMockData()
    {
        // id, name, user, start and end dates
        requestBooking(101, "Cordless Drill", "Sam", LocalDate.now().plusDays(1), LocalDate.now().plusDays(4));
        requestBooking(102, "Lawn Mower", "Sam", LocalDate.now().plusDays(0), LocalDate.now().plusDays(5));
        requestBooking(103, "3D Printer", "Sam", LocalDate.now().plusDays(0), LocalDate.now().plusDays(2));
    }

    // Validates and registers new requests
    // checks if date and fields are valid @return true if valid, false otherwise
    public boolean requestBooking(int resourceId, String resourceName, String username, LocalDate startDate, LocalDate endDate)
    {
        if (startDate == null || endDate == null) {return false;}
        if (endDate.isBefore(startDate)) {return false;}
        if (username == null || username.isBlank() || resourceName == null || resourceName.isBlank()) {return false;}
        Booking booking = new Booking(idCount++, resourceId, resourceName, username,startDate,endDate, BookingStatus.REQUESTED);
        arrayBookings.add(booking);
        return true;
    }
    public List<Booking> getUserBookings(String username)
    {
        return arrayBookings.stream()
                .filter(booking -> booking.getBorrowerUsername().equalsIgnoreCase(username))
                .collect(Collectors.toList());
    }
    public List<Booking> getAllBookings() {return new ArrayList<>(arrayBookings);}
    public boolean cancelBooking(int bookingId)
    {
        for (Booking booking : arrayBookings)
        {
            if (booking.getId() == bookingId && booking.getStatus() == BookingStatus.REQUESTED)
            {
                booking.setStatus(BookingStatus.CANCELLED);
                return true;
            }
        }
        return false;
    }
    public void markOnLoan(Booking booking) {
        if (booking.getStatus() != BookingStatus.APPROVED) {
            throw new IllegalStateException("Booking must be approved before it can be marked on loan.");
        }

        booking.setStatus(BookingStatus.ON_LOAN);
    }

    public void markReturned(Booking booking)
    {
        if (booking.getStatus() != BookingStatus.ON_LOAN)
        {
            throw new IllegalStateException("Booking must be on loan before it can be returned.");
        }
        booking.setStatus(BookingStatus.RETURNED);
    }
}
