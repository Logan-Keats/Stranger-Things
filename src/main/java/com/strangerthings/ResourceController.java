package com.strangerthings;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ResourceController {

    private Runnable addResourceNavigation;
    private ResourceNavigation resourceDetailsNavigation;

    @FXML
    private TextField searchField;

    @FXML
    private VBox cordlessDrillCard;

    @FXML
    private VBox lawnMowerCard;

    @FXML
    private VBox printerCard;

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
        String searchText = searchField.getText().trim().toLowerCase();

        boolean showDrill =
                "Cordless Drill".toLowerCase().contains(searchText)
                        || "Tools".toLowerCase().contains(searchText)
                        || "David".toLowerCase().contains(searchText);

        boolean showMower =
                "Lawn Mower".toLowerCase().contains(searchText)
                        || "Garden Equipment".toLowerCase().contains(searchText)
                        || "Sarah".toLowerCase().contains(searchText);

        boolean showPrinter =
                "3D Printer".toLowerCase().contains(searchText)
                        || "Electronics".toLowerCase().contains(searchText)
                        || "Michael".toLowerCase().contains(searchText);

        cordlessDrillCard.setVisible(showDrill);
        cordlessDrillCard.setManaged(showDrill);

        lawnMowerCard.setVisible(showMower);
        lawnMowerCard.setManaged(showMower);

        printerCard.setVisible(showPrinter);
        printerCard.setManaged(showPrinter);
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