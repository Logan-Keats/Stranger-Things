package com.strangerthings;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AddResourceController {

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
        System.out.println("Cancel clicked");
    }

    @FXML
    private void onRegisterResource() {

        boolean valid = resourceService.isValidResource(
                resourceNameField.getText(),
                descriptionField.getText(),
                categoryBox.getValue(),
                collectionMethodBox.getValue()
        );

        if (valid) {
            errorLabel.setText("");
            System.out.println("Register resource clicked");
        } else {
            errorLabel.setText("Please complete all resource details.");
        }
    }
}