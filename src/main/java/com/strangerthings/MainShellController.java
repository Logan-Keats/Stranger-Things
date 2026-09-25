package com.strangerthings;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

import com.strangerthings.model.Booking;
import com.strangerthings.controller.BookingDetailController;

/**
 * Shared application shell and integration point for Sections 2-6.
 */
public class MainShellController {

    @FXML private Label userNameLabel;
    @FXML private Button allBookingsButton;
    @FXML private Button reportsButton;
    @FXML private StackPane contentArea;

    private Role loggedInRole = Role.MEMBER;

    private String selectedResourceName;
    private String selectedResourceCategory;
    private String selectedResourceOwner;
    private String selectedResourceDescription;
    private String selectedResourceCollectionMethod;

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
        showResources();
    }

    @FXML
    private void onMyBookings()
    {
        loadPage("/com/strangerthings/booking-view.fxml");
    }

    @FXML
    private void onAllBookings() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainShellController.class.getResource(
                            "/com/strangerthings/approve.fxml"
                    )
            );

            Parent view = loader.load();

            ApproveController controller = loader.getController();
            controller.setBookingDetailNavigation(this::showBookingDetail);

            contentArea.getChildren().setAll(view);

        } catch (IOException | RuntimeException exception) {
            showUnavailablePage("approve.fxml");
        }
    }

    @FXML
    private void onBookingDetail() {
        showUnavailablePage(
                "Select a booking from All Bookings to view its details."
        );
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

    private void showResourceDetails(
            String name,
            String category,
            String owner,
            String description,
            String collectionMethod) {

        selectedResourceName = name;
        selectedResourceCategory = category;
        selectedResourceOwner = owner;
        selectedResourceDescription = description;
        selectedResourceCollectionMethod = collectionMethod;

        try {
            FXMLLoader loader = new FXMLLoader(
                    MainShellController.class.getResource(
                            "/com/strangerthings/resource-detail.fxml"
                    )
            );

            Parent view = loader.load();

            ResourceDetailController controller = loader.getController();

            controller.setResource(
                    name,
                    category,
                    owner,
                    description,
                    collectionMethod
            );

            controller.setBackNavigation(this::showResources);
            controller.setEditResourceNavigation(this::showEditResource);

            contentArea.getChildren().setAll(view);

        } catch (IOException | RuntimeException exception) {
            showUnavailablePage("resource-detail.fxml");
        }
    }

    private void showResources() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainShellController.class.getResource(
                            "/com/strangerthings/resource.fxml"
                    )
            );

            Parent view = loader.load();

            ResourceController controller = loader.getController();

            controller.setAddResourceNavigation(this::showAddResource);
            controller.setResourceDetailsNavigation(this::showResourceDetails);

            contentArea.getChildren().setAll(view);

        } catch (IOException | RuntimeException exception) {
            showUnavailablePage("resource.fxml");
        }
    }

    private void showEditResource() {
        loadResourcePage(
                "/com/strangerthings/edit-resource.fxml",
                () -> showResourceDetails(
                        selectedResourceName,
                        selectedResourceCategory,
                        selectedResourceOwner,
                        selectedResourceDescription,
                        selectedResourceCollectionMethod
                ),
                null
        );
    }

    private void showBookingDetail(Booking booking) {
        if (booking == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    MainShellController.class.getResource(
                            "/com/strangerthings/booking-detail.fxml"
                    )
            );

            Parent view = loader.load();

            BookingDetailController controller = loader.getController();
            controller.setBooking(booking);

            contentArea.getChildren().setAll(view);

        } catch (IOException | RuntimeException exception) {
            showUnavailablePage("booking-detail.fxml");
        }
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
