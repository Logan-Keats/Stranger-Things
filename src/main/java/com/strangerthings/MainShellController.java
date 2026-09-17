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
    public void setLoggedInUser(String username) {
        if (userNameLabel != null) {
            userNameLabel.setText(username);
        }
    }

    @FXML
    private void onResources() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainShellController.class.getResource(
                            "/com/strangerthings/resource.fxml"
                    )
            );

            Parent resourcePage = loader.load();

            ResourceController controller = loader.getController();

            controller.setAddResourceNavigation(this::showAddResource);
            controller.setResourceDetailsNavigation(this::showResourceDetails);
            contentArea.getChildren().setAll(resourcePage);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void showAddResource() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainShellController.class.getResource(
                            "/com/strangerthings/add-resource.fxml"
                    )
            );

            Parent addResourcePage = loader.load();

            AddResourceController controller = loader.getController();
            controller.setCancelNavigation(this::showResources);

            contentArea.getChildren().setAll(addResourcePage);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void showResourceDetails() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainShellController.class.getResource(
                            "/com/strangerthings/resource-detail.fxml"
                    )
            );

            Parent resourceDetailPage = loader.load();

            ResourceDetailController controller = loader.getController();
            controller.setBackNavigation(this::showResources);

            contentArea.getChildren().setAll(resourceDetailPage);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void showResources() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainShellController.class.getResource(
                            "/com/strangerthings/resource.fxml"
                    )
            );

            Parent resourcePage = loader.load();

            ResourceController controller = loader.getController();

            controller.setAddResourceNavigation(this::showAddResource);
            controller.setResourceDetailsNavigation(this::showResourceDetails);

            contentArea.getChildren().setAll(resourcePage);

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
