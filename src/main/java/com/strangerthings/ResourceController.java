package com.strangerthings;

import javafx.fxml.FXML;

public class ResourceController {

    private Runnable addResourceNavigation;

    public void setAddResourceNavigation(Runnable addResourceNavigation) {
        this.addResourceNavigation = addResourceNavigation;
    }

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
        if (addResourceNavigation != null) {
            addResourceNavigation.run();
        }
    }
}
