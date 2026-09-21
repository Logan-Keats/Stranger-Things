package com.strangerthings;

import java.time.LocalDate;
import java.util.List;

import com.strangerthings.model.Booking;
import com.strangerthings.model.BookingStatus;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/** Admin booking management backed by the shared booking service. */
public class ApproveController {
    private final ApprovalService approvalService = new ApprovalService();
    private final BookingService bookingService = BookingService.getInstance();

    @FXML private TableView<Booking> bookingTable;
    @FXML private TableColumn<Booking, Integer> idColumn;
    @FXML private TableColumn<Booking, String> resourceColumn;
    @FXML private TableColumn<Booking, String> borrowerColumn;
    @FXML private TableColumn<Booking, LocalDate> startDateColumn;
    @FXML private TableColumn<Booking, LocalDate> endDateColumn;
    @FXML private TableColumn<Booking, BookingStatus> statusColumn;
    @FXML private Label messageLabel;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        resourceColumn.setCellValueFactory(new PropertyValueFactory<>("resourceName"));
        borrowerColumn.setCellValueFactory(new PropertyValueFactory<>("borrowerUsername"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        loadBookings();
    }

    @FXML
    private void onApprove() {
        Booking booking = bookingTable.getSelectionModel().getSelectedItem();
        if (booking == null) {
            messageLabel.setText("Select a booking from the table.");
            return;
        }
        boolean approved = approvalService.approveBooking(booking, bookingService.getAllBookings());
        if (approved && bookingService.saveStatus(booking)) {
            messageLabel.setText("Booking approved.");
            loadBookings();
        } else {
            messageLabel.setText("Booking could not be approved.");
        }
    }

    @FXML
    private void onReject() {
        Booking booking = bookingTable.getSelectionModel().getSelectedItem();
        if (booking == null) {
            messageLabel.setText("Select a booking from the table.");
            return;
        }
        boolean rejected = approvalService.rejectBooking(booking);
        if (rejected && bookingService.saveStatus(booking)) {
            messageLabel.setText("Booking rejected.");
            loadBookings();
        } else {
            messageLabel.setText("Booking could not be rejected.");
        }
    }

    private void loadBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        bookingTable.setItems(FXCollections.observableArrayList(bookings));
    }
}
