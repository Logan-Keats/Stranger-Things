package com.strangerthings;

import com.strangerthings.model.Booking;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.util.List;

public class ApproveController {

    private final ApprovalService approvalService = new ApprovalService();

    private Booking booking;
    private List<Booking> existingBookings;

    public void setBooking(Booking booking, List<Booking> existingBookings) {
        this.booking = booking;
        this.existingBookings = existingBookings;
    }

    @FXML
    private void onApprove(ActionEvent event) {

        if (booking == null) {
            System.out.println("No booking selected.");
            return;
        }

        boolean approved = approvalService.approveBooking(
                booking,
                existingBookings
        );

        if (approved) {
            System.out.println("Booking approved.");
        } else {
            System.out.println("Booking could not be approved.");
        }
    }

    @FXML
    private void onReject(ActionEvent event) {

        if (booking == null) {
            System.out.println("No booking selected.");
            return;
        }

        boolean rejected = approvalService.rejectBooking(booking);

        if (rejected) {
            System.out.println("Booking rejected.");
        } else {
            System.out.println("Booking could not be rejected.");
        }
    }
}