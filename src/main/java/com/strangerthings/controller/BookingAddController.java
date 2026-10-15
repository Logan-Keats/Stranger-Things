package com.strangerthings.controller;

import com.strangerthings.model.Resource;
import com.strangerthings.service.BookingService;
import com.strangerthings.service.UserSession;

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
    private Resource resource;
    private Runnable cancelNavigation;

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
        String activeUser = UserSession.username();

        if (activeUser.isBlank()) {
            errorLabel.setText("Please log in before creating a booking request.");
            return;
        }

        boolean success = bookingService.requestBooking(resource.getId(), resource.getName(), activeUser, startDate, endDate);

        if (success)
        {
            if (parentController != null)
            {
                parentController.loadBookings();
            }
            if (cancelNavigation != null) {
                cancelNavigation.run();
            }
        }
        else
        {
            errorLabel.setText("Invalid Booking: Please Check Date and Name of Item!");
        }
    }

    @FXML
    private void onCancel() {
        if (cancelNavigation != null) {
            cancelNavigation.run();
        }
    }

    private void closeWindow()
    {
        Stage stage = (Stage) resourceNameField.getScene().getWindow();
        stage.close();
    }

    public void setResource(Resource resource) {
        this.resource = resource;

        if (resource != null) {
            resourceNameField.setText(resource.getName());
            resourceNameField.setEditable(false);
        }
    }

    public void setCancelNavigation(Runnable cancelNavigation) {
        this.cancelNavigation = cancelNavigation;
    }
}
