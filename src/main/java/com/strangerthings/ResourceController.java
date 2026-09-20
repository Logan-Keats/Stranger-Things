package com.strangerthings;

import javafx.fxml.FXML;

public class ResourceController {

    private Runnable addResourceNavigation;
    private ResourceNavigation resourceDetailsNavigation;

    public interface ResourceNavigation {
        void openResource(
                String name,
                String category,
                String owner,
                String description,
                String collectionMethod
        );
    }

    public void setAddResourceNavigation(Runnable addResourceNavigation) {
        this.addResourceNavigation = addResourceNavigation;
    }

    public void setResourceDetailsNavigation(ResourceNavigation resourceDetailsNavigation) {
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
    private void onCordlessDrill() {
        if (resourceDetailsNavigation != null) {
            resourceDetailsNavigation.openResource(
                    "Cordless Drill",
                    "Tools",
                    "David",
                    "Cordless power drill suitable for basic household repairs and DIY projects.",
                    "Pick Up"
            );
        }
    }

    @FXML
    private void onLawnMower() {
        if (resourceDetailsNavigation != null) {
            resourceDetailsNavigation.openResource(
                    "Lawn Mower",
                    "Garden Equipment",
                    "Sarah",
                    "Electric lawn mower suitable for small to medium-sized lawns.",
                    "Pick Up"
            );
        }
    }

    @FXML
    private void on3DPrinter() {
        if (resourceDetailsNavigation != null) {
            resourceDetailsNavigation.openResource(
                    "3D Printer",
                    "Electronics",
                    "Michael",
                    "3D printer available for small personal projects and prototype printing.",
                    "Pick Up"
            );
        }
    }
}