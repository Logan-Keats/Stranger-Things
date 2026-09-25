package com.strangerthings.controller;

import com.strangerthings.BookingService;
import com.strangerthings.model.Booking;
import com.strangerthings.model.BookingStatus;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
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

    @FXML
    private Button markOnLoanButton;

    @FXML
    private Button markReturnedButton;

    @FXML
    private Label messageLabel;

    private Booking booking;

    private final BookingService bookingService =
            BookingService.getInstance();

    public void setBooking(Booking booking) {
        this.booking = booking;
        updateBookingDetails();
    }

    private void updateBookingDetails() {
        if (booking == null) {
            return;
        }

        bookingIdLabel.setText(String.valueOf(booking.getId()));
        resourceNameLabel.setText(booking.getResourceName());
        borrowerNameLabel.setText(booking.getBorrowerName());
        statusLabel.setText(booking.getStatus().toString());

        updateButtons();
    }

    private void updateButtons() {
        BookingStatus status = booking.getStatus();

        markOnLoanButton.setDisable(
                status != BookingStatus.APPROVED
        );

        markReturnedButton.setDisable(
                status != BookingStatus.ON_LOAN
        );
    }

    @FXML
    private void onMarkOnLoan() {
        if (booking == null) {
            return;
        }

        try {
            bookingService.markOnLoan(booking);
            messageLabel.setText("Booking marked as on loan.");
            updateBookingDetails();
        } catch (IllegalStateException exception) {
            messageLabel.setText(exception.getMessage());
        }
    }

    @FXML
    private void onMarkReturned() {
        if (booking == null) {
            return;
        }

        try {
            bookingService.markReturned(booking);
            messageLabel.setText("Booking marked as returned.");
            updateBookingDetails();
        } catch (IllegalStateException exception) {
            messageLabel.setText(exception.getMessage());
        }
    }
}