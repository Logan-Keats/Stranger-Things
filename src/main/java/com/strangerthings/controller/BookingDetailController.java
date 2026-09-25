package com.strangerthings.controller;

import java.time.LocalDate;
import java.util.List;

import com.strangerthings.BookingService;
import com.strangerthings.model.Booking;
import com.strangerthings.model.BookingStatus;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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

    @FXML
    private TableView<Booking> historyTable;

    @FXML
    private TableColumn<Booking, String> historyBorrowerColumn;

    @FXML
    private TableColumn<Booking, LocalDate> historyStartDateColumn;

    @FXML
    private TableColumn<Booking, LocalDate> historyEndDateColumn;

    @FXML
    private TableColumn<Booking, BookingStatus> historyStatusColumn;

    private Booking booking;

    private final BookingService bookingService =
            BookingService.getInstance();

    @FXML
    private void initialize() {
        historyBorrowerColumn.setCellValueFactory(
                new PropertyValueFactory<>("borrowerUsername")
        );

        historyStartDateColumn.setCellValueFactory(
                new PropertyValueFactory<>("startDate")
        );

        historyEndDateColumn.setCellValueFactory(
                new PropertyValueFactory<>("endDate")
        );

        historyStatusColumn.setCellValueFactory(
                new PropertyValueFactory<>("status")
        );
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
        updateBookingDetails();
        loadResourceHistory();
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

    private void loadResourceHistory() {
        if (booking == null) {
            return;
        }

        List<Booking> history =
                bookingService.getResourceHistory(
                        booking.getResourceId()
                );

        historyTable.setItems(
                FXCollections.observableArrayList(history)
        );
    }

    @FXML
    private void onMarkOnLoan() {
        if (booking == null) {
            return;
        }

        try {
            bookingService.markOnLoan(booking);
            messageLabel.setText(
                    "Booking marked as on loan."
            );

            updateBookingDetails();
            loadResourceHistory();

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
            messageLabel.setText(
                    "Booking marked as returned."
            );

            updateBookingDetails();
            loadResourceHistory();

        } catch (IllegalStateException exception) {
            messageLabel.setText(exception.getMessage());
        }
    }
}