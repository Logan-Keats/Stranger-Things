package com.strangerthings;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/** Creates a new member or admin account, then returns the user to login. */
public class RegisterController {
    private final AuthService authService = new AuthService();

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    @FXML
    private void onRegisterMember(ActionEvent event) {
        register(event, Role.MEMBER);
    }

    @FXML
    private void onRegisterAdmin(ActionEvent event) {
        register(event, Role.ADMIN);
    }

    private void register(ActionEvent event, Role role) {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();
        String confirmation = confirmPasswordField.getText() == null ? "" : confirmPasswordField.getText();

        if (username.isBlank() || password.isBlank()) {
            errorLabel.setText("Enter a username and password");
            return;
        }
        if (!password.equals(confirmation)) {
            errorLabel.setText("Passwords do not match");
            return;
        }
        if (!authService.register(username, password, role)) {
            errorLabel.setText("Username is taken, or password is fewer than 4 characters");
            return;
        }
        showLogin(event);
    }

    @FXML
    private void onBackToLogin(ActionEvent event) {
        showLogin(event);
    }

    private void showLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(RegisterController.class.getResource("/com/strangerthings/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
        } catch (IOException exception) {
            errorLabel.setText("Failed to return to login");
        }
    }
}
