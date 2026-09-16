package com.strangerthings;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class AddResourceController {

    @FXML
    private ComboBox<String> categoryBox;

    @FXML
    private ComboBox<String> collectionMethodBox;

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
        System.out.println("Register Resource clicked");
    }
}