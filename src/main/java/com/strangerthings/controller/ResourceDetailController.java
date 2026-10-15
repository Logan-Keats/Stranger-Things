package com.strangerthings.controller;

import com.strangerthings.service.BookingService;
import com.strangerthings.model.Resource;
import com.strangerthings.dao.ResourceDao;
import com.strangerthings.dao.SqliteResourceDao;

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

    @FXML
    private Label availabilityLabel;

    private Resource resource;

    private Runnable backNavigation;
    private Runnable editResourceNavigation;
    private Runnable bookingNavigation;
    private Runnable deleteNavigation;

    private final ResourceDao resourceDao = new SqliteResourceDao();

    private final BookingService bookingService = BookingService.getInstance();

    public void setResource(Resource resource) {
        this.resource = resource;

        resourceNameLabel.setText(resource.getName());
        categoryLabel.setText(resource.getCategory());
        ownerLabel.setText(resource.getOwnerUsername());
        descriptionLabel.setText(resource.getDescription());
        collectionMethodLabel.setText(resource.getCollectionMethod());

        boolean available = bookingService.isResourceAvailable(resource.getId());

        availabilityLabel.setText(
                available ? "AVAILABLE" : "UNAVAILABLE - ON LOAN"
        );
    }

    public Resource getResource() {
        return resource;
    }

    public void setBackNavigation(Runnable backNavigation) {
        this.backNavigation = backNavigation;
    }

    public void setEditResourceNavigation(Runnable editResourceNavigation) {
        this.editResourceNavigation = editResourceNavigation;
    }

    public void setBookingNavigation(Runnable bookingNavigation) {
        this.bookingNavigation = bookingNavigation;
    }

    public void setDeleteNavigation(Runnable deleteNavigation) {
        this.deleteNavigation = deleteNavigation;
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
    private void onRequestBooking() {
        if (bookingNavigation != null) {
            bookingNavigation.run();
        }
    }

    @FXML
    private void onRemoveResource() {
        if (resource == null) {
            return;
        }

        boolean deleted = resourceDao.delete(resource.getId());

        if (deleted && deleteNavigation != null) {
            deleteNavigation.run();
        }
    }
}