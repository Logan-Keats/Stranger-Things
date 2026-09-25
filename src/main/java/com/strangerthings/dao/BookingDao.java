package com.strangerthings.dao;

import java.util.List;

import com.strangerthings.model.Booking;
import com.strangerthings.model.BookingStatus;

/** Persistent booking operations used by member and admin booking views. */
public interface BookingDao {
    boolean create(Booking booking);
    List<Booking> findAll();
    List<Booking> findByBorrower(String username);
    List<Booking> findByResourceId(int resourceId);
    boolean updateStatus(int bookingId, BookingStatus status);
}
