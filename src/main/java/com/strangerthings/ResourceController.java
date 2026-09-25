package com.strangerthings;

import java.util.List;

import com.strangerthings.dao.ResourceDao;
import com.strangerthings.dao.SqliteResourceDao;
import com.strangerthings.model.Resource;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public class ResourceController {

    @FXML
    private TextField searchField;

    @FXML
    private TilePane resourceTilePane;

    private final ResourceDao resourceDao = new SqliteResourceDao();

    private Runnable addResourceNavigation;
    private ResourceNavigation resourceDetailsNavigation;

    public interface ResourceNavigation {
        void openResource(Resource resource);
    }

    @FXML
    private void initialize() {
        displayResources(resourceDao.findAll());
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

        List<Resource> resources = resourceDao.findAll();

        if (!searchText.isEmpty()) {
            resources = resources.stream()
                    .filter(resource ->
                            resource.getName().toLowerCase().contains(searchText)
                                    || resource.getCategory().toLowerCase().contains(searchText)
                                    || resource.getOwnerUsername().toLowerCase().contains(searchText))
                    .toList();
        }

        displayResources(resources);
    }

    @FXML
    private void onAddResource() {
        if (addResourceNavigation != null) {
            addResourceNavigation.run();
        }
    }

    private void displayResources(List<Resource> resources) {
        resourceTilePane.getChildren().clear();

        for (Resource resource : resources) {
            VBox card = createResourceCard(resource);
            resourceTilePane.getChildren().add(card);
        }
    }

    private VBox createResourceCard(Resource resource) {
        VBox card = new VBox(5);
        card.setStyle("-fx-cursor: hand;");

        Label imageLabel = new Label("[Resource Image]");
        imageLabel.setPrefWidth(200);
        imageLabel.setPrefHeight(200);
        imageLabel.setAlignment(javafx.geometry.Pos.CENTER);
        imageLabel.setStyle(
                "-fx-background-color: #2b2b2b;" +
                        "-fx-text-fill: white;"
        );

        HBox nameRow = createDetailRow(
                "Resource Name:",
                resource.getName()
        );

        HBox categoryRow = createDetailRow(
                "Category:",
                resource.getCategory()
        );

        HBox ownerRow = createDetailRow(
                "Owner:",
                resource.getOwnerUsername()
        );

        card.getChildren().addAll(
                imageLabel,
                nameRow,
                categoryRow,
                ownerRow
        );

        card.setOnMouseClicked(event -> {
            if (resourceDetailsNavigation != null) {
                resourceDetailsNavigation.openResource(resource);
            }
        });

        return card;
    }

    private HBox createDetailRow(String title, String value) {
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold;");

        Label valueLabel = new Label(value);

        HBox row = new HBox(5);
        row.getChildren().addAll(titleLabel, valueLabel);

        return row;
    }
}