package com.strangerthings;

import javafx.fxml.FXML;

public class ResourceController {

    @FXML
    private void onAddFilters() {
        System.out.println("Filters clicked");
    }

    @FXML
    private void onSearch() {
        System.out.println("Search clicked");
    }

    @FXML
    private void onAddResource() {
        System.out.println("Add Resource clicked");
    }
}
