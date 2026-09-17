package com.strangerthings;

import javafx.fxml.FXML;

public class ResourceDetailController {

    private Runnable backNavigation;

    public void setBackNavigation(Runnable backNavigation) {
        this.backNavigation = backNavigation;
    }

    @FXML
    private void onBack() {
        if (backNavigation != null) {
            backNavigation.run();
        }
    }

    @FXML
    private void onEditResource() {
        System.out.println("Edit Resource clicked");
    }

    @FXML
    private void onRemoveResource() {
        System.out.println("Remove Resource clicked");
    }
}