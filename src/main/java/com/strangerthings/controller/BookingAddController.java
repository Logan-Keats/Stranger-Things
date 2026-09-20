package com.strangerthings.controller;

import com.strangerthings.BookingService;

import javafx.fxml.FXML;

import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.stage.Stage;

import java.time.LocalDate;

public class BookingAddController
{
    // Event Handler
    // Listens to Inputs
    // Outputs to BookingService

    @FXML private TextField resourceNameField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Label errorLabel;

    private final BookingService bookingService = BookingService.getInstance();
    private BookingViewController parentController;

    public void setParentController(BookingViewController parentController)
    {
        this.parentController = parentController;
    }

    @FXML
    private void onSubmit()
    {
        String resourceName = resourceNameField.getText() == null ? "" : resourceNameField.getText().trim();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String activeUser = "Sam"; // Mock session user

        boolean success = bookingService.requestBooking(1, resourceName, activeUser, startDate, endDate);

        if (success)
        {
            if (parentController != null)
            {
                parentController.loadBookings();
            }
            closeWindow();
        }
        else
        {
            errorLabel.setText("Invalid Booking: Please Check Date and Name of Item!");
        }
    }

    @FXML
    private void onCancel()
    {
        closeWindow();
    }

    private void closeWindow()
    {
        Stage stage = (Stage) resourceNameField.getScene().getWindow();
        stage.close();
    }
}
