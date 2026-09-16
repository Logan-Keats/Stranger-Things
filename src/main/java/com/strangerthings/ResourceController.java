package com.strangerthings;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ResourceController {

    @FXML
    private TextField resourceField;

    @FXML
    private void onAddFilters() {
        System.out.println("Filters clicked");
    }
    @FXML
    private void onSearch() {
        String searchText = resourceField.getText();
        System.out.println("Searching for: " + searchText);
    }
    @FXML
    private void onAddResource() {
        System.out.println("Add Resource clicked");
    }
}
