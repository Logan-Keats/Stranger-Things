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

/**
 * Controller for {@code login.fxml}.
 * Handles Login button clicks; delegates credential checks to {@link AuthService}.
 */
public class LoginController {

    /** Shared login rules (stub accounts for now; later may use a database). */
    private final AuthService authService = new AuthService();

    /** Bound to the username TextField in login.fxml. */
    @FXML
    private TextField usernameField;

    /** Bound to the password PasswordField in login.fxml. */
    @FXML
    private PasswordField passwordField;

    /** Shows US-1.3 error text when login fails; cleared on success. */
    @FXML
    private Label errorLabel;

    /**
     * Login button handler ({@code onAction="#onLogin"}).
     * On success: load main-shell and replace this Stage's Scene (US-1.1).
     * On failure: stay on login and set {@link #errorLabel} (US-1.3).
     */
    @FXML
    private void onLogin(ActionEvent event) {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        User user = authService.login(username, password);

        if (user != null) {
            errorLabel.setText("");
            UserSession.setCurrentUser(user);
            try {
                FXMLLoader loader = new FXMLLoader(
                        LoginController.class.getResource("/com/strangerthings/main-shell.fxml"));
                Parent root = loader.load();

                MainShellController shellController = loader.getController();
                shellController.setLoggedInUser(user.getUsername(), user.getRole());

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root, 600, 400));
            } catch (IOException ex) {
                errorLabel.setText("Failed to load main screen");
            }
        } else {
            errorLabel.setText("Invalid username or password");
        }
    }

    @FXML
    private void onRegister(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(LoginController.class.getResource("/com/strangerthings/register.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
        } catch (IOException exception) {
            errorLabel.setText("Failed to load registration");
        }
    }
}
