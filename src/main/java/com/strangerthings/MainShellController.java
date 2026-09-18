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

public class MainShellController {

    @FXML
    private Label userNameLabel;

    @FXML
    private Button logoutButton;

    @FXML
    private StackPane contentArea;

    /** Called after login succeeds (C-5). */
    public void setLoggedInUser(String username, Role role) {
        if (userNameLabel != null) {
            userNameLabel.setText(username);
        }

        if (role != Role.ADMIN) {
            allBookingsButton.setVisible(false);
            allBookingsButton.setManaged(false);
        }
    }

    @FXML
    private Button allBookingsButton;

    @FXML
    private void showApprovePage() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainShellController.class.getResource("/com/strangerthings/approve.fxml"));

            Parent page = loader.load();

            contentArea.getChildren().setAll(page);

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
