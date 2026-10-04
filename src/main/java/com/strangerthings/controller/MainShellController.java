package com.strangerthings.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;

import com.strangerthings.model.Role;
import com.strangerthings.model.User;
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
import com.strangerthings.model.Resource;
import com.strangerthings.dao.ResourceDao;
import com.strangerthings.dao.SqliteResourceDao;
/**
 * Shared application shell and integration point for Sections 2-6.
 */
public class MainShellController {

    @FXML private Label userNameLabel;
    @FXML private Button allBookingsButton;
    @FXML private Button reportsButton;
    @FXML private Button backButton;
    @FXML private Button forwardButton;
    @FXML private StackPane contentArea;

    private Role loggedInRole = Role.MEMBER;
    private String loggedInUsername;
    private Resource selectedResource;

    private final Deque<Runnable> backStack = new ArrayDeque<>();
    private final Deque<Runnable> forwardStack = new ArrayDeque<>();
    private Runnable currentLocation;
    private boolean navigatingHistory;

    @FXML
    private void initialize() {
        updateAdminNavigation();
        updateHistoryButtons();
    }

    /** Retains compatibility with the current login flow. */
    public void setLoggedInUser(String username) {
        setLoggedInUser(username, "admin".equalsIgnoreCase(username) ? Role.ADMIN : Role.MEMBER);
    }

    /** Supports the role-aware login hand-off used by the other sections. */
    public void setLoggedInUser(String username, Role role) {
        loggedInUsername = username;
        loggedInRole = role == null ? Role.MEMBER : role;
        userNameLabel.setText(username);
        updateAdminNavigation();
        backStack.clear();
        forwardStack.clear();
        currentLocation = null;
        goTo(this::renderHome);
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
        goTo(() -> loadPageContent("/com/strangerthings/booking-view.fxml"));
    }

    @FXML
    private void onAllBookings() {
        goTo(this::renderAllBookings);
    }



    @FXML
    private void onReports() {
        if (loggedInRole == Role.ADMIN) {
            goTo(() -> loadPageContent("/com/strangerthings/reports.fxml"));
        }
    }

    @FXML
    private void onNavigateBack() {
        if (backStack.isEmpty() || currentLocation == null) {
            return;
        }
        navigatingHistory = true;
        forwardStack.push(currentLocation);
        currentLocation = backStack.pop();
        currentLocation.run();
        navigatingHistory = false;
        updateHistoryButtons();
    }

    @FXML
    private void onNavigateForward() {
        if (forwardStack.isEmpty() || currentLocation == null) {
            return;
        }
        navigatingHistory = true;
        backStack.push(currentLocation);
        currentLocation = forwardStack.pop();
        currentLocation.run();
        navigatingHistory = false;
        updateHistoryButtons();
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

    private void goTo(Runnable location) {
        if (location == null) {
            return;
        }
        if (!navigatingHistory && currentLocation != null) {
            backStack.push(currentLocation);
            forwardStack.clear();
        }
        currentLocation = location;
        location.run();
        updateHistoryButtons();
    }

    private void updateHistoryButtons() {
        boolean canGoBack = !backStack.isEmpty();
        boolean canGoForward = !forwardStack.isEmpty();
        if (backButton != null) {
            backButton.setDisable(!canGoBack);
        }
        if (forwardButton != null) {
            forwardButton.setDisable(!canGoForward);
        }
    }

    private void showHome() {
        goTo(this::renderHome);
    }

    private void renderHome() {
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
        goTo(this::renderAddResource);
    }

    private void renderAddResource() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainShellController.class.getResource(
                            "/com/strangerthings/add-resource.fxml"
                    )
            );

            Parent view = loader.load();

            AddResourceController controller = loader.getController();

            controller.setCancelNavigation(this::showResources);
            controller.setLoggedInUsername(loggedInUsername);

            contentArea.getChildren().setAll(view);

        } catch (IOException | RuntimeException exception) {
            exception.printStackTrace();
            showUnavailablePage("add-resource.fxml");
        }
    }

    private void showResourceDetails(Resource resource) {

        if (resource == null) {
            return;
        }

        selectedResource = resource;
        goTo(() -> renderResourceDetails(resource));
    }

    private void renderResourceDetails(Resource resource) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainShellController.class.getResource(
                            "/com/strangerthings/resource-detail.fxml"
                    )
            );

            Parent view = loader.load();

            ResourceDetailController controller = loader.getController();

            controller.setResource(resource);

            controller.setBackNavigation(this::showResources);
            controller.setEditResourceNavigation(this::showEditResource);
            controller.setDeleteNavigation(this::showResources);

            contentArea.getChildren().setAll(view);

        } catch (IOException | RuntimeException exception) {
            exception.printStackTrace();
            showUnavailablePage("resource-detail.fxml");
        }
    }

    private void showResources() {
        goTo(this::renderResources);
    }

    private void renderResources() {
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

        if (selectedResource == null) {
            return;
        }

        Resource editing = selectedResource;
        goTo(() -> renderEditResource(editing));
    }

    private void renderEditResource(Resource editing) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainShellController.class.getResource(
                            "/com/strangerthings/edit-resource.fxml"
                    )
            );

            Parent view = loader.load();

            EditResourceController controller = loader.getController();

            controller.setResource(editing);

            controller.setCancelNavigation(
                    () -> showResourceDetails(editing)
            );

            controller.setSaveNavigation(() -> {
                ResourceDao resourceDao = new SqliteResourceDao();

                Resource updatedResource =
                        resourceDao.findById(editing.getId());

                if (updatedResource != null) {
                    showResourceDetails(updatedResource);
                } else {
                    showResources();
                }
            });

            contentArea.getChildren().setAll(view);

        } catch (IOException | RuntimeException exception) {
            exception.printStackTrace();
            showUnavailablePage("edit-resource.fxml");
        }
    }

    private void showBookingDetail(Booking booking) {
        if (booking == null) {
            return;
        }

        goTo(() -> renderBookingDetail(booking));
    }

    private void renderBookingDetail(Booking booking) {
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

    private void renderAllBookings() {
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


    private void loadPageContent(String resourcePath) {
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
