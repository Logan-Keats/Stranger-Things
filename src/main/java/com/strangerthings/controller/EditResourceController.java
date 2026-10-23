package com.strangerthings.controller;

import com.strangerthings.service.ResourceService;
import com.strangerthings.dao.ResourceDao;
import com.strangerthings.dao.SqliteResourceDao;
import com.strangerthings.model.Resource;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;

public class EditResourceController {

    @FXML
    private TextField resourceNameField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ComboBox<String> categoryBox;

    @FXML
    private ComboBox<String> collectionMethodBox;

    @FXML
    private Label errorLabel;

    @FXML
    private ImageView imagePreview;

    private String selectedImagePath;

    private Runnable cancelNavigation;
    private Runnable saveNavigation;

    private Resource resource;

    private final ResourceService resourceService = new ResourceService();
    private final ResourceDao resourceDao = new SqliteResourceDao();

    @FXML
    private void initialize() {
        categoryBox.getItems().addAll(
                "Tools",
                "Electronics",
                "Garden",
                "Kitchen",
                "Sports",
                "Other"
        );

        collectionMethodBox.getItems().addAll(
                "Pick Up",
                "Delivery",
                "Meet Up"
        );
    }

    public void setResource(Resource resource) {
        this.resource = resource;

        resourceNameField.setText(resource.getName());
        descriptionField.setText(resource.getDescription());
        categoryBox.setValue(resource.getCategory());
        collectionMethodBox.setValue(resource.getCollectionMethod());
    }

    @FXML
    private void onUploadImage()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Set Resource Image");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showSaveDialog(resourceNameField.getScene().getWindow());

        if (selectedFile != null)
        {
            selectedImagePath = selectedFile.getAbsolutePath();
            Image image = new Image(selectedFile.toURI().toString());
            if (imagePreview != null)
            {
                imagePreview.setImage(image);
            }
        }
    }

    @FXML
    private void onCancel() {
        if (cancelNavigation != null) {
            cancelNavigation.run();
        }
    }

    @FXML
    private void onSaveChanges() {

        boolean valid = resourceService.isValidResource(
                resourceNameField.getText(),
                descriptionField.getText(),
                categoryBox.getValue(),
                collectionMethodBox.getValue()
        );

        if (!valid) {
            errorLabel.setText("Please complete all resource details.");
            return;
        }

        if (resource == null) {
            errorLabel.setText("Unable to find resource.");
            return;
        }

        Resource updatedResource = new Resource(
                resource.getId(),
                resourceNameField.getText().trim(),
                categoryBox.getValue(),
                resource.getStatus(),
                resource.getOwnerUsername(),
                descriptionField.getText().trim(),
                collectionMethodBox.getValue(),
                resource.getEstimatedSavings(),
                resource.getCo2AvoidedKg()
        );

        boolean updated = resourceDao.update(updatedResource);

        if (updated) {
            resource = updatedResource;
            errorLabel.setText("");

            if (saveNavigation != null) {
                saveNavigation.run();
            }
        } else {
            errorLabel.setText("Unable to update resource.");
        }
    }

    public void setCancelNavigation(Runnable cancelNavigation) {
        this.cancelNavigation = cancelNavigation;
    }

    public void setSaveNavigation(Runnable saveNavigation) {
        this.saveNavigation = saveNavigation;
    }
}