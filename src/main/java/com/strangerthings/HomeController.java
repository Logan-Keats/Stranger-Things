package com.strangerthings;

import java.util.Locale;
import java.util.Map;

import com.strangerthings.service.ReportService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

/** Controller for the member and admin Home dashboards. */
public class HomeController {

    private final ReportService reportService = new ReportService();

    @FXML private Label pageTitleLabel;
    @FXML private Label pageSubtitleLabel;
    @FXML private VBox memberDashboard;
    @FXML private VBox adminDashboard;

    @FXML private Label totalSharedLabel;
    @FXML private Label totalBorrowedLabel;
    @FXML private Label co2AvoidedLabel;
    @FXML private Label moneySavedLabel;
    @FXML private PieChart memberCategoryPieChart;
    @FXML private ListView<String> recentActivityList;

    @FXML private Label totalNumberLabel;
    @FXML private Label activeListingsLabel;
    @FXML private Label totalBorrowingsLabel;
    @FXML private Label usageRateLabel;
    @FXML private PieChart adminCategoryPieChart;
    @FXML private BarChart<String, Number> borrowingActivityChart;
    @FXML private ListView<String> recentJoinList;

    @FXML
    private void initialize() {
        populateMemberDashboard();
        populateAdminDashboard();
        setAdminView(false);
    }

    void setAdminView(boolean isAdmin) {
        setDashboardVisible(memberDashboard, !isAdmin);
        setDashboardVisible(adminDashboard, isAdmin);
        pageTitleLabel.setText(isAdmin ? "Home" : "Home Dashboard");
        pageSubtitleLabel.setText(isAdmin ? "Analytics overview" : "Community sharing overview");
    }

    private void populateMemberDashboard() {
        String username = UserSession.username();
        totalSharedLabel.setText(String.valueOf(reportService.getTotalSharedResources(username)));
        totalBorrowedLabel.setText(String.valueOf(reportService.getTotalBorrowedResources(username)));
        co2AvoidedLabel.setText(String.format(Locale.US, "%.1f kg", reportService.getCo2AvoidedKg(username)));
        moneySavedLabel.setText(String.format(Locale.US, "$%.2f", reportService.getTotalSavings(username)));
        memberCategoryPieChart.setData(categoryData(reportService.getCategoryDistribution(username), true));
        recentActivityList.getItems().setAll(reportService.getRecentActivities(username));
    }

    private void populateAdminDashboard() {
        totalNumberLabel.setText(String.valueOf(reportService.getTotalMembers()));
        activeListingsLabel.setText(String.valueOf(reportService.getActiveListings()));
        totalBorrowingsLabel.setText(String.valueOf(reportService.getTotalBorrowedResources()));
        usageRateLabel.setText(reportService.getUsageRatePercent() + "%");
        adminCategoryPieChart.setData(categoryData(reportService.getCategoryDistribution(), false));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        reportService.getBorrowingActivityByDay()
                .forEach((day, count) -> series.getData().add(new XYChart.Data<>(day, count)));
        borrowingActivityChart.getData().clear();
        borrowingActivityChart.getData().add(series);
        recentJoinList.getItems().setAll(reportService.getRecentJoinStats());
    }

    private javafx.collections.ObservableList<PieChart.Data> categoryData(Map<String, Integer> distribution,
            boolean includeCount) {
        return FXCollections.observableArrayList(
                distribution.entrySet().stream()
                        .map(entry -> new PieChart.Data(
                                includeCount ? entry.getKey() + " (" + entry.getValue() + ")" : entry.getKey(),
                                entry.getValue()))
                        .toList());
    }

    private void setDashboardVisible(Node dashboard, boolean visible) {
        dashboard.setVisible(visible);
        dashboard.setManaged(visible);
    }

    @FXML
    private void onShowMembers(MouseEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("All users");
        dialog.setHeaderText("Registered administrators and members");
        ListView<String> members = new ListView<>();
        members.getItems().setAll(reportService.getAllMembers().stream()
                .map(member -> member.username() + "  |  " + member.role() + "  |  joined " + member.joinedAt())
                .toList());
        members.setPrefSize(420, 260);
        dialog.getDialogPane().setContent(members);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }
}
