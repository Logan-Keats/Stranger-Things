package com.strangerthings.controller;

import com.strangerthings.BookingService;
import com.strangerthings.model.Booking;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class BookingDetailController {

    @FXML
    private Label bookingIdLabel;

    @FXML
    private Label resourceNameLabel;

    @FXML
    private Label borrowerNameLabel;

    @FXML
    private Label statusLabel;

    private Booking booking;

    private final BookingService bookingService = new BookingService();

    public void setBooking(Booking booking) {
        this.booking = booking;
        updateBookingDetails();
    }

    private void updateBookingDetails() {
        bookingIdLabel.setText(String.valueOf(booking.getId()));
        resourceNameLabel.setText(booking.getResourceName());
        borrowerNameLabel.setText(booking.getBorrowerName());
        statusLabel.setText(booking.getStatus().toString());
    }

    @FXML
    private void onMarkOnLoan() {
        bookingService.markOnLoan(booking);
        updateBookingDetails();
    }

    @FXML
    private void onMarkReturned() {
        bookingService.markReturned(booking);
        updateBookingDetails();
    }
}