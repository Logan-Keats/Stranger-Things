package com.strangerthings;

import javafx.fxml.FXML;

public class ResourceDetailController {

    private Runnable backNavigation;
    private Runnable editResourceNavigation;

    public void setBackNavigation(Runnable backNavigation) {
        this.backNavigation = backNavigation;
    }

    public void setEditResourceNavigation(Runnable editResourceNavigation) {
        this.editResourceNavigation = editResourceNavigation;
    }

    @FXML
    private void onBack() {
        if (backNavigation != null) {
            backNavigation.run();
        }
    }

    @FXML
    private void onEditResource() {
        if (editResourceNavigation != null) {
            editResourceNavigation.run();
        }
    }

    @FXML
    private void onRemoveResource() {
        System.out.println("Remove Resource clicked");
    }
}