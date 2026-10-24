package com.strangerthings.controller;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.function.Consumer;

import com.strangerthings.service.ApprovalService;
import com.strangerthings.service.BookingService;
import com.strangerthings.model.Booking;
import com.strangerthings.model.BookingStatus;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

/** Admin booking management backed by the shared booking service. */
public class ApproveController {

    /**
     * Stylesheet for this screen, kept here because no .css file is used.
     * It is attached to the root pane in initialize().
     */
    private static final String CSS = """
            .approve-root {
                -fx-background-color: #f3eee5;
            }
     
            .page-title {
                -fx-font-size: 26px;
                -fx-font-weight: bold;
                -fx-text-fill: #24201e;
            }
            .page-subtitle {
                -fx-font-size: 14px;
                -fx-text-fill: #776f69;
            }
            .section-title {
                -fx-font-size: 20px;
                -fx-font-weight: bold;
                -fx-text-fill: #302b28;
            }
            .message-label {
                -fx-font-size: 13px;
                -fx-text-fill: #776f69;
            }
        
            .status-tabs {
                -fx-background-color: transparent;
                -fx-border-color: transparent;
                -fx-tab-min-height: 34;
            }
            .status-tabs .tab-header-background {
                -fx-background-color: transparent;
            }
            .status-tabs .tab-content-area {
                -fx-background-color: transparent;
                -fx-border-color: transparent;
            }
            .status-tabs .tab {
                -fx-background-color: #ebe4d7;
                -fx-background-radius: 8 8 0 0;
                -fx-border-color: #d5c8b6;
                -fx-border-radius: 8 8 0 0;
                -fx-padding: 6 14 6 14;
                -fx-focus-color: transparent;
                -fx-faint-focus-color: transparent;
            }
            .status-tabs .tab:hover {
                -fx-background-color: #e2d9c8;
            }
            .status-tabs .tab:selected {
                -fx-background-color: #6f8065;
                -fx-border-color: #6f8065;
            }
            .status-tabs .tab .tab-label {
                -fx-text-fill: #625a54;
            }
            .status-tabs .tab:selected .tab-label {
                -fx-text-fill: white;
                -fx-font-weight: bold;
            }
        
            .booking-table {
                -fx-background-color: #fcfaf6;
                -fx-border-color: #d9cdb9;
                -fx-border-radius: 12;
                -fx-background-radius: 12;
            }
            .booking-table .column-header-background {
                -fx-background-color: #ebe4d7;
                -fx-background-radius: 12 12 0 0;
            }
            .booking-table .column-header,
            .booking-table .filler {
                -fx-background-color: transparent;
                -fx-size: 38;
                -fx-border-color: transparent transparent #d9cdb9 transparent;
            }
            .booking-table .column-header .label {
                -fx-text-fill: #625a54;
                -fx-font-weight: bold;
                -fx-font-size: 12px;
            }
            .booking-table .table-row-cell {
                -fx-background-color: #fcfaf6;
                -fx-table-cell-border-color: #eee7dc;
            }
            .booking-table .table-row-cell:odd {
                -fx-background-color: #f8f4ec;
            }
            .booking-table .table-row-cell:hover {
                -fx-background-color: #efe8da;
            }
            .booking-table .table-row-cell:selected {
                -fx-background-color: #6f8065;
            }
            .booking-table .table-cell {
                -fx-text-fill: #302b28;
                -fx-padding: 8 10 8 10;
            }
            .booking-table .table-row-cell:selected .table-cell {
                -fx-text-fill: white;
            }
       
            .btn-approve {
                -fx-background-color: #6f8065;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-background-radius: 8;
                -fx-cursor: hand;
            }
            .btn-approve:hover {
                -fx-background-color: #5f6f56;
            }
            .btn-reject {
                -fx-background-color: #fcfaf6;
                -fx-text-fill: #8b5148;
                -fx-border-color: #d9bdb5;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                -fx-cursor: hand;
            }
            .btn-reject:hover {
                -fx-background-color: #f6ebe8;
            }
            .btn-details {
                -fx-background-color: #ebe4d7;
                -fx-text-fill: #625a54;
                -fx-border-color: #d5c8b6;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                -fx-cursor: hand;
            }
            .btn-details:hover {
                -fx-background-color: #e2d9c8;
            }
        """;

    private final ApprovalService approvalService =
            new ApprovalService();

    private final BookingService bookingService =
            BookingService.getInstance();

    private Consumer<Booking> bookingDetailNavigation;

    @FXML private BorderPane root;

    @FXML private TableView<Booking> bookingTable;

    @FXML private TableColumn<Booking, Integer> idColumn;
    @FXML private TableColumn<Booking, String> resourceColumn;
    @FXML private TableColumn<Booking, String> borrowerColumn;
    @FXML private TableColumn<Booking, LocalDate> startDateColumn;
    @FXML private TableColumn<Booking, LocalDate> endDateColumn;

    @FXML private TabPane statusTabs;

    @FXML private Tab pendingTab;
    @FXML private Tab approvedTab;
    @FXML private Tab rejectedTab;
    @FXML private Tab cancelledTab;
    @FXML private Tab onLoanTab;
    @FXML private Tab returnedTab;

    @FXML private Label messageLabel;

    @FXML
    private void initialize() {

        // Attach the embedded stylesheet to this screen.
        applyStyles();
        fillViewportHeight();

        // Connect table columns to Booking properties.
        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("id"));

        resourceColumn.setCellValueFactory(
                new PropertyValueFactory<>("resourceName"));

        borrowerColumn.setCellValueFactory(
                new PropertyValueFactory<>("borrowerUsername"));

        startDateColumn.setCellValueFactory(
                new PropertyValueFactory<>("startDate"));

        endDateColumn.setCellValueFactory(
                new PropertyValueFactory<>("endDate"));

        // Reload the table whenever a different tab is selected.
        statusTabs.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldTab, newTab) -> {
                    loadBookings();
                });

        // Pending is selected by default.
        statusTabs.getSelectionModel()
                .select(pendingTab);

        loadBookings();
    }

    /** Adds the CSS string above to the root pane as a data: URI. */
    private void applyStyles() {
        String encoded = Base64.getEncoder().encodeToString(
                CSS.getBytes(StandardCharsets.UTF_8));
        root.getStylesheets().add("data:text/css;base64," + encoded);
    }

    private void fillViewportHeight() {
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) {
                return;
            }

            Node node = root.getParent();
            while (node != null && !(node instanceof ScrollPane)) {
                node = node.getParent();
            }

            if (node instanceof ScrollPane scrollPane) {
                root.minHeightProperty().bind(
                        Bindings.createDoubleBinding(
                                () -> scrollPane.getViewportBounds().getHeight(),
                                scrollPane.viewportBoundsProperty()));
            }
        });
    }

    /** Allows the main shell to control navigation to Booking Detail. */
    public void setBookingDetailNavigation(
            Consumer<Booking> bookingDetailNavigation) {

        this.bookingDetailNavigation = bookingDetailNavigation;
    }

    /** Opens Booking Detail for the selected booking. */
    @FXML
    private void onViewDetails() {

        Booking booking =
                bookingTable.getSelectionModel().getSelectedItem();

        if (booking == null) {
            messageLabel.setText(
                    "Select a booking from the table.");
            return;
        }

        if (bookingDetailNavigation != null) {
            bookingDetailNavigation.accept(booking);
        }
    }

    @FXML
    private void onApprove() {

        Booking booking =
                bookingTable.getSelectionModel().getSelectedItem();

        if (booking == null) {
            messageLabel.setText(
                    "Select a booking from the table.");
            return;
        }

        // Only allow approval from the Pending tab.
        if (booking.getStatus() != BookingStatus.REQUESTED) {
            messageLabel.setText(
                    "Only pending bookings can be approved.");
            return;
        }

        boolean approved = approvalService.approveBooking(
                booking,
                bookingService.getAllBookings());

        if (approved && bookingService.saveStatus(booking)) {
            messageLabel.setText("Booking approved.");
            loadBookings();
        } else {
            messageLabel.setText(
                    "Booking could not be approved.");
        }
    }

    @FXML
    private void onReject() {

        Booking booking =
                bookingTable.getSelectionModel().getSelectedItem();

        if (booking == null) {
            messageLabel.setText(
                    "Select a booking from the table.");
            return;
        }

        // Only allow rejection from the Pending tab.
        if (booking.getStatus() != BookingStatus.REQUESTED) {
            messageLabel.setText(
                    "Only pending bookings can be rejected.");
            return;
        }

        boolean rejected = approvalService.rejectBooking(booking);

        if (rejected && bookingService.saveStatus(booking)) {
            messageLabel.setText("Booking rejected.");
            loadBookings();
        } else {
            messageLabel.setText(
                    "Booking could not be rejected.");
        }
    }

    /**
     * Loads bookings matching the currently selected status tab.
     */
    private void loadBookings() {

        List<Booking> allBookings =
                bookingService.getAllBookings();

        Tab selectedTab = statusTabs.getSelectionModel()
                .getSelectedItem();

        BookingStatus selectedStatus =
                getStatusForTab(selectedTab);

        List<Booking> filteredBookings = allBookings.stream()
                .filter(booking ->
                        booking.getStatus() == selectedStatus)
                .toList();

        bookingTable.setItems(
                FXCollections.observableArrayList(
                        filteredBookings));

        // Clear any selection left over from the previous tab.
        bookingTable.getSelectionModel().clearSelection();
    }

    /**
     * Maps each tab to its corresponding booking status.
     */
    private BookingStatus getStatusForTab(Tab tab) {

        if (tab == approvedTab) {
            return BookingStatus.APPROVED;
        }

        if (tab == rejectedTab) {
            return BookingStatus.REJECTED;
        }

        if (tab == cancelledTab) {
            return BookingStatus.CANCELLED;
        }

        if (tab == onLoanTab) {
            return BookingStatus.ON_LOAN;
        }

        if (tab == returnedTab) {
            return BookingStatus.RETURNED;
        }

        // Pending is the default tab.
        return BookingStatus.REQUESTED;
    }
}