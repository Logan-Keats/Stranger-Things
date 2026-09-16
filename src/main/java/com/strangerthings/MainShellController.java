package com.strangerthings;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.strangerthings.controller.BookingDetailController;
import com.strangerthings.model.Booking;
import com.strangerthings.model.BookingStatus;

public class MainShellController {

    @FXML
    private Label userNameLabel;

    @FXML
    private Button logoutButton;

    @FXML
    private StackPane contentArea;


    /** Called after login succeeds (C-5). */
    public void setLoggedInUser(String username) {
        if (userNameLabel != null) {
            userNameLabel.setText(username);
        }
    }

    @FXML
    private void onBookingDetail() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainShellController.class.getResource(
                            "/com/strangerthings/booking-detail.fxml"));

            Parent bookingDetail = loader.load();

            BookingDetailController controller = loader.getController();

            // Temporary sample booking for CP3 prototype.
            // Replace with selected booking when the shared booking flow is integrated.

            Booking booking = new Booking(
                    1,
                    "Power Drill",
                    "member",
                    BookingStatus.APPROVED
            );

            controller.setBooking(booking);

            contentArea.getChildren().setAll(bookingDetail);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void onLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainShellController.class.getResource("/com/strangerthings/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
        } catch (IOException ex) {
            // Stub: no error UI on shell yet
            ex.printStackTrace();
        }
    }
}
