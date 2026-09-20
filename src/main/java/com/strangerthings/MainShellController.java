package com.strangerthings;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Shared application shell and integration point for Sections 2-6.
 */
public class MainShellController {

    @FXML private Label userNameLabel;
    @FXML private Button allBookingsButton;
    @FXML private Button reportsButton;
    @FXML private StackPane contentArea;

    private Role loggedInRole = Role.MEMBER;

    @FXML
    private void initialize() {
        updateAdminNavigation();
    }

    /** Retains compatibility with the current login flow. */
    public void setLoggedInUser(String username) {
        setLoggedInUser(username, "admin".equalsIgnoreCase(username) ? Role.ADMIN : Role.MEMBER);
    }

    /** Supports the role-aware login hand-off used by the other sections. */
    public void setLoggedInUser(String username, Role role) {
        loggedInRole = role == null ? Role.MEMBER : role;
        userNameLabel.setText(username);
        updateAdminNavigation();
        showHome();
    }

    /** Supports callers that already have an authenticated User instance. */
    public void setLoggedInUser(User user) {
        if (user != null) {
            setLoggedInUser(user.getUsername(), user.getRole());
        }
    }

    @FXML
    private void onHome() {
        showHome();
    }

    @FXML
    private void onResources() {
        loadResourcePage("/com/strangerthings/resource.fxml", this::showAddResource, this::showResourceDetails);
    }

    @FXML
    private void onMyBookings() {
        loadPage("/com/strangerthings/booking-view.fxml");
    }

    @FXML
    private void onAllBookings() {
        loadPage("/com/strangerthings/approve.fxml");
    }

    @FXML
    private void onBookingDetail() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainShellController.class.getResource("/com/strangerthings/booking-detail.fxml"));
            Parent view = loader.load();
            configurePrototypeBooking(loader.getController());
            contentArea.getChildren().setAll(view);
        } catch (IOException | RuntimeException exception) {
            showUnavailablePage("booking-detail.fxml");
        }
    }

    @FXML
    private void onReports() {
        if (loggedInRole == Role.ADMIN) {
            loadPage("/com/strangerthings/reports.fxml");
        }
    }

    @FXML
    private void onLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                    MainShellController.class.getResource("/com/strangerthings/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void showHome() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainShellController.class.getResource("/com/strangerthings/home.fxml"));
            Parent view = loader.load();
            loader.<HomeController>getController().setAdminView(loggedInRole == Role.ADMIN);
            contentArea.getChildren().setAll(view);
        } catch (IOException | RuntimeException exception) {
            showUnavailablePage("Home");
        }
    }

    private void showAddResource() {
        loadResourcePage("/com/strangerthings/add-resource.fxml", this::showResources, null);
    }

    private void showResourceDetails() {
        loadResourcePage("/com/strangerthings/resource-detail.fxml", this::showResources, this::showEditResource);
    }

    private void showResources() {
        loadResourcePage("/com/strangerthings/resource.fxml", this::showAddResource, this::showResourceDetails);
    }

    private void showEditResource() {
        loadResourcePage("/com/strangerthings/edit-resource.fxml", this::showResourceDetails, null);
    }

    private void loadResourcePage(String resourcePath, Runnable backNavigation, Runnable detailNavigation) {
        try {
            FXMLLoader loader = new FXMLLoader(MainShellController.class.getResource(resourcePath));
            Parent view = loader.load();
            configureResourceController(loader.getController(), backNavigation, detailNavigation);
            contentArea.getChildren().setAll(view);
        } catch (IOException | RuntimeException exception) {
            showUnavailablePage(resourcePath.substring(resourcePath.lastIndexOf('/') + 1));
        }
    }

    private void configureResourceController(Object controller, Runnable backNavigation, Runnable detailNavigation) {
        invokeNavigationSetter(controller, "setAddResourceNavigation", backNavigation);
        invokeNavigationSetter(controller, "setCancelNavigation", backNavigation);
        invokeNavigationSetter(controller, "setBackNavigation", backNavigation);
        invokeNavigationSetter(controller, "setResourceDetailsNavigation", detailNavigation);
        invokeNavigationSetter(controller, "setEditResourceNavigation", detailNavigation);
    }

    private void invokeNavigationSetter(Object controller, String methodName, Runnable navigation) {
        if (controller == null || navigation == null) {
            return;
        }
        try {
            Method method = controller.getClass().getMethod(methodName, Runnable.class);
            method.invoke(controller, navigation);
        } catch (NoSuchMethodException exception) {
            // This page does not expose this optional navigation callback.
        } catch (IllegalAccessException | InvocationTargetException exception) {
            throw new IllegalStateException("Unable to configure " + methodName, exception);
        }
    }

    /**
     * Preserves S5's temporary prototype booking once its model/controller
     * classes are present.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void configurePrototypeBooking(Object controller) {
        if (controller == null) {
            return;
        }
        try {
            Class<?> statusClass = Class.forName("com.strangerthings.model.BookingStatus");
            Object approved = Enum.valueOf((Class<? extends Enum>) statusClass.asSubclass(Enum.class), "APPROVED");
            Class<?> bookingClass = Class.forName("com.strangerthings.model.Booking");
            Constructor<?> constructor = bookingClass.getConstructor(int.class, String.class, String.class, statusClass);
            Object booking = constructor.newInstance(1, "Power Drill", "member", approved);
            controller.getClass().getMethod("setBooking", bookingClass).invoke(controller, booking);
        } catch (ClassNotFoundException | NoSuchMethodException exception) {
            // S5 has not been merged yet, so the booking page remains standalone.
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            throw new IllegalStateException("Unable to configure the S5 booking detail page.", exception);
        }
    }

    private void loadPage(String resourcePath) {
        try {
            Parent view = FXMLLoader.load(MainShellController.class.getResource(resourcePath));
            contentArea.getChildren().setAll(view);
        } catch (IOException | RuntimeException exception) {
            showUnavailablePage(resourcePath.substring(resourcePath.lastIndexOf('/') + 1));
        }
    }

    private void updateAdminNavigation() {
        boolean isAdmin = loggedInRole == Role.ADMIN;
        allBookingsButton.setVisible(isAdmin);
        allBookingsButton.setManaged(isAdmin);
        reportsButton.setVisible(isAdmin);
        reportsButton.setManaged(isAdmin);
    }

    private void showUnavailablePage(String pageName) {
        contentArea.getChildren().setAll(new Label("Page unavailable: " + pageName));
    }
}
