package com.strangerthings;

import com.strangerthings.dao.ResourceDao;
import com.strangerthings.dao.SqliteResourceDao;
import com.strangerthings.model.Resource;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AddResourceController {

    private Runnable cancelNavigation;
    private String loggedInUsername;

    @FXML
    private TextField resourceNameField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ComboBox<String> categoryBox;

    @FXML
    private ComboBox<String> collectionMethodBox;

    @FXML
    private Label errorLabel;

    private final ResourceService resourceService = new ResourceService();
    private final ResourceDao resourceDao = new SqliteResourceDao();

    @FXML
    private void initialize() {
        categoryBox.getItems().addAll(
                "Tools",
                "Electronics",
                "Garden",
                "Kitchen",
                "Sports",
                "Other"
        );

        collectionMethodBox.getItems().addAll(
                "Pick Up",
                "Delivery",
                "Meet Up"
        );
    }

    @FXML
    private void onUploadImage() {
        System.out.println("Upload Image clicked");
    }

    @FXML
    private void onCancel() {
        if (cancelNavigation != null) {
            cancelNavigation.run();
        }
    }

    @FXML
    private void onRegisterResource() {

        boolean valid = resourceService.isValidResource(
                resourceNameField.getText(),
                descriptionField.getText(),
                categoryBox.getValue(),
                collectionMethodBox.getValue()
        );

        if (!valid) {
            errorLabel.setText("Please complete all resource details.");
            return;
        }

        if (loggedInUsername == null || loggedInUsername.isBlank()) {
            errorLabel.setText("Unable to identify the logged in user.");
            return;
        }

        Resource resource = new Resource(
                resourceNameField.getText().trim(),
                categoryBox.getValue(),
                "ACTIVE",
                loggedInUsername,
                descriptionField.getText().trim(),
                collectionMethodBox.getValue(),
                0.0,
                0.0
        );

        boolean created = resourceDao.create(resource);

        if (created) {
            errorLabel.setText("");

            if (cancelNavigation != null) {
                cancelNavigation.run();
            }
        } else {
            errorLabel.setText("Unable to register resource.");
        }
    }

    public void setCancelNavigation(Runnable cancelNavigation) {
        this.cancelNavigation = cancelNavigation;
    }

    public void setLoggedInUsername(String loggedInUsername) {
        this.loggedInUsername = loggedInUsername;
    }
}