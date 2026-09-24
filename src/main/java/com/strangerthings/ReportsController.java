package com.strangerthings;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Locale;

import com.strangerthings.service.ReportService;
import com.strangerthings.service.ReportService.AuditEvent;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/** Controller for the admin-only Reports and Audit view. */
public class ReportsController {

    private final ReportService reportService = new ReportService();

    @FXML private TableView<AuditEvent> auditTable;
    @FXML private TableColumn<AuditEvent, String> actionColumn;
    @FXML private TableColumn<AuditEvent, String> userColumn;
    @FXML private TableColumn<AuditEvent, String> timeColumn;
    @FXML private TextField auditSearchField;
    @FXML private ComboBox<String> auditTypeFilter;
    @FXML private Label exportStatusLabel;

    private FilteredList<AuditEvent> filteredAuditEvents;

    @FXML
    private void initialize() {
        setupAuditLog();
    }

    @FXML private void onExportUsage(ActionEvent event) { exportReport(event, "Usage"); }
    @FXML private void onExportCo2(ActionEvent event) { exportReport(event, "CO2 avoided"); }
    @FXML private void onExportMemberActivity(ActionEvent event) { exportReport(event, "Member Activity"); }
    @FXML private void onExportModerationLog(ActionEvent event) { exportReport(event, "Moderation Log"); }

    @FXML
    private void onAuditTypeChanged(ActionEvent event) {
        applyAuditFilters();
    }

    private void setupAuditLog() {
        actionColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().action()));
        userColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().user()));
        timeColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().time()));

        ObservableList<AuditEvent> events = FXCollections.observableArrayList(reportService.getAuditEvents());
        filteredAuditEvents = new FilteredList<>(events);
        auditTable.setItems(filteredAuditEvents);
        auditTypeFilter.getItems().setAll("All types");
        events.stream().map(AuditEvent::type).distinct().sorted().forEach(auditTypeFilter.getItems()::add);
        auditTypeFilter.setValue("All types");
        auditSearchField.textProperty().addListener((observable, oldValue, newValue) -> applyAuditFilters());
    }

    private void applyAuditFilters() {
        String searchText = auditSearchField.getText() == null ? "" : auditSearchField.getText().trim().toLowerCase(Locale.ROOT);
        String selectedType = auditTypeFilter.getValue();
        filteredAuditEvents.setPredicate(event -> {
            boolean matchesType = selectedType == null || "All types".equals(selectedType)
                    || event.type().equalsIgnoreCase(selectedType);
            boolean matchesSearch = searchText.isEmpty()
                    || event.action().toLowerCase(Locale.ROOT).contains(searchText)
                    || event.user().toLowerCase(Locale.ROOT).contains(searchText)
                    || event.time().toLowerCase(Locale.ROOT).contains(searchText);
            return matchesType && matchesSearch;
        });
    }

    private void exportReport(ActionEvent event, String reportName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export " + reportName + " CSV");
        fileChooser.setInitialFileName(toFileName(reportName));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));

        Window owner = ((Node) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showSaveDialog(owner);
        if (selectedFile == null) {
            exportStatusLabel.setText("Export cancelled");
            return;
        }

        try {
            Files.writeString(selectedFile.toPath(), reportService.buildCsv(reportName), StandardCharsets.UTF_8);
            exportStatusLabel.setText("Exported " + selectedFile.getName());
        } catch (IOException ex) {
            exportStatusLabel.setText("Export failed: " + ex.getMessage());
        }
    }

    private String toFileName(String reportName) {
        return reportName.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "")
                + "-report.csv";
    }
}
