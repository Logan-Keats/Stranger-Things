package com.strangerthings;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ResourceDetailController {

    @FXML
    private Label resourceNameLabel;

    @FXML
    private Label categoryLabel;

    @FXML
    private Label ownerLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label collectionMethodLabel;

    private Runnable backNavigation;
    private Runnable editResourceNavigation;

    public void setResource(
            String name,
            String category,
            String owner,
            String description,
            String collectionMethod) {

        resourceNameLabel.setText(name);
        categoryLabel.setText(category);
        ownerLabel.setText(owner);
        descriptionLabel.setText(description);
        collectionMethodLabel.setText(collectionMethod);
    }

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