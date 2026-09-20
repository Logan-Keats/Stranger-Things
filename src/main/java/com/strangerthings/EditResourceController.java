package com.strangerthings;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class EditResourceController {

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

    private Runnable cancelNavigation;

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

        // Temporary prototype values until database is implemented
        categoryBox.setValue("Tools");
        collectionMethodBox.setValue("Pick Up");
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
    private void onSaveChanges() {
        System.out.println("Save Changes clicked");
    }

    public void setCancelNavigation(Runnable cancelNavigation) {
        this.cancelNavigation = cancelNavigation;
    }
}