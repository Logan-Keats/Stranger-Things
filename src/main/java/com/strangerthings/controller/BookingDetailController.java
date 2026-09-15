package com.strangerthings.controller;

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

    public void setBooking(Booking booking) {
        this.booking = booking;

        bookingIdLabel.setText(String.valueOf(booking.getId()));
        resourceNameLabel.setText(booking.getResourceName());
        borrowerNameLabel.setText(booking.getBorrowerName());
        statusLabel.setText(booking.getStatus().toString());
    }
}