package com.strangerthings.controller;

import com.strangerthings.BookingService;
import com.strangerthings.UserSession;
import com.strangerthings.model.Booking;
import com.strangerthings.model.BookingStatus;

import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;




public class BookingViewController {
    @FXML
    private TableView<Booking> bookingTable;
    @FXML
    private TableColumn<Booking, Integer> idColumn;
    @FXML
    private TableColumn<Booking, String> resourceColumn;
    @FXML
    private TableColumn<Booking, LocalDate> startDateColumn;
    @FXML
    private TableColumn<Booking, LocalDate> endDateColumn;
    @FXML
    private TableColumn<Booking, BookingStatus> statusColumn;

    private final BookingService bookingService = BookingService.getInstance();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        resourceColumn.setCellValueFactory(new PropertyValueFactory<>("resourceName"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadBookings();
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

            Stage stage = new Stage();
            stage.setTitle("New Booking Request");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e)
        {
            e.printStackTrace();
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
