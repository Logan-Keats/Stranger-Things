package com.strangerthings.controller;

import com.strangerthings.service.BookingService;
import com.strangerthings.service.UserSession;
import com.strangerthings.model.Booking;
import com.strangerthings.model.BookingStatus;

import java.time.LocalDate;
import java.util.function.Consumer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;




public class BookingViewController {
    @FXML private TableView<Booking> bookingTable;
    @FXML private TableColumn<Booking, Integer> idColumn;
    @FXML private TableColumn<Booking, String> resourceColumn;
    @FXML private TableColumn<Booking, LocalDate> startDateColumn;
    @FXML private TableColumn<Booking, LocalDate> endDateColumn;
    @FXML private TableColumn<Booking, BookingStatus> statusColumn;
    @FXML private Label messageLabel;

    private final BookingService bookingService = BookingService.getInstance();
    private Consumer<Booking> bookingDetailNavigation;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        resourceColumn.setCellValueFactory(new PropertyValueFactory<>("resourceName"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // enables double-click drill down menu for booking details
        bookingTable.setRowFactory(tv ->
        {
            javafx.scene.control.TableRow<Booking> row = new javafx.scene.control.TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openBookingDetail(row.getItem());
                }
            });
            return row;
        });

        loadBookings();
    }

    public void setBookingDetailNavigation(Consumer<Booking> bookingDetailNavigation) {
        this.bookingDetailNavigation = bookingDetailNavigation;
    }

    public void loadBookings() {
        ObservableList<Booking> bookings = FXCollections.observableArrayList(
                bookingService.getUserBookings(UserSession.username()));
        bookingTable.setItems(bookings);
    }

    @FXML
    private void onRefresh() {
        loadBookings();
    }

    @FXML
    void onOpenAddDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/strangerthings/booking-add.fxml"));
            Parent root = loader.load();

            BookingAddController addController = loader.getController();
            addController.setParentController(this);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("New Booking Request");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    private void onViewDetails() {
        Booking selected = bookingTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openBookingDetail(selected);
        } else if (messageLabel != null) {
            messageLabel.setText("Select a booking from the table.");
        }
    }

    private void openBookingDetail(Booking booking) {
        if (bookingDetailNavigation != null) {
            bookingDetailNavigation.accept(booking);
        }
    }



    @FXML
    private void onCancelSelected() {
        Booking selected = bookingTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            bookingService.cancelBooking(selected.getId());
            loadBookings();
        }
    }
}
