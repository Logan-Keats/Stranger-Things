package com.strangerthings;

import javafx.fxml.FXML;

public class ResourceController {

    private Runnable addResourceNavigation;
    private Runnable resourceDetailsNavigation;

    public void setAddResourceNavigation(Runnable addResourceNavigation) {
        this.addResourceNavigation = addResourceNavigation;
    }

    public void setResourceDetailsNavigation(Runnable resourceDetailsNavigation) {
        this.resourceDetailsNavigation = resourceDetailsNavigation;
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

    @FXML
    private void onResourceDetails() {
        if (resourceDetailsNavigation != null) {
            resourceDetailsNavigation.run();
        }
    }
}
