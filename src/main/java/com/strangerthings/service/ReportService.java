package com.strangerthings.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.strangerthings.dao.ReportDao;

/**
 * Pure Java reporting logic for Home and Reports screens.
 */
public class ReportService {

    private final ReportDao reportDao = new ReportDao();

    private final List<ResourceRecord> resources = List.of(
            new ResourceRecord("Cordless drill", "Tools", true, 2, 45.00, 5.0),
            new ResourceRecord("Extension ladder", "Tools", true, 1, 32.50, 4.2),
            new ResourceRecord("Projector", "Tools", false, 0, 0.00, 3.1),
            new ResourceRecord("Stand mixer", "Kitchen", true, 1, 20.00, 2.2),
            new ResourceRecord("Slow cooker", "Kitchen", true, 1, 15.00, 1.5),
            new ResourceRecord("Camping tent", "Outdoor", false, 1, 20.00, 2.5));

    private final List<AuditEvent> auditEvents = List.of(
            new AuditEvent("Requested Extension ladder", "Member 1", "26/08 09:10"),
            new AuditEvent("Flagged Projector listing", "Member 2", "26/08 11:25"),
            new AuditEvent("Returned Camping tent", "Member 3", "27/08 15:40"),
            new AuditEvent("Approved Stand mixer", "Admin", "27/08 16:05"));

    public Map<String, Integer> getCategoryDistribution() {
        Map<String, Integer> databaseDistribution = reportDao.getCategoryDistribution();
        if (!databaseDistribution.isEmpty()) {
            return databaseDistribution;
        }
        return resources.stream()
                .collect(Collectors.groupingBy(
                        ResourceRecord::category,
                        LinkedHashMap::new,
                        Collectors.summingInt(resource -> 1)));
    }

    public Map<String, Integer> getBorrowingActivityByDay() {
        Map<String, Integer> activity = new LinkedHashMap<>();
        activity.put("24/8", 1);
        activity.put("25/8", 2);
        activity.put("26/8", 1);
        activity.put("27/8", 2);
        return activity;
    }

    public int getTotalSharedResources() {
        int databaseCount = reportDao.getTotalSharedCount();
        return databaseCount > 0 ? databaseCount : resources.size();
    }

    public int getTotalBorrowedResources() {
        return resources.stream()
                .mapToInt(ResourceRecord::borrowCount)
                .sum();
    }

    public int getActiveListings() {
        if (reportDao.getTotalSharedCount() > 0) {
            return reportDao.getActiveListingCount();
        }
        return (int) resources.stream()
                .filter(ResourceRecord::active)
                .count();
    }

    public double getTotalSavings() {
        return resources.stream()
                .mapToDouble(ResourceRecord::savings)
                .sum();
    }

    public double getCo2AvoidedKg() {
        return resources.stream()
                .mapToDouble(ResourceRecord::co2AvoidedKg)
                .sum();
    }

    public int getUsageRatePercent() {
        if (resources.isEmpty()) {
            return 0;
        }
        return (int) Math.round((getActiveListings() * 100.0) / getTotalSharedResources());
    }

    public List<AuditEvent> getAuditEvents() {
        return auditEvents;
    }

    public List<String> getRecentActivities() {
        return auditEvents.stream()
                .map(event -> event.action() + " - " + event.time())
                .toList();
    }

    public List<String> getRecentJoinStats() {
        return List.of(
                "Member 50 joined - 1d",
                "Member 49 joined - 2d",
                "Member 48 joined - 3d",
                "Member 47 joined - 4d");
    }

    public String buildCsv(String reportName) {
        String normalizedReportName = reportName == null || reportName.isBlank()
                ? "Usage"
                : reportName.trim();

        if ("Moderation Log".equalsIgnoreCase(normalizedReportName)) {
            return buildModerationCsv();
        }

        List<CsvMetric> metrics = switch (normalizedReportName.toLowerCase()) {
            case "co2 avoided" -> List.of(
                    new CsvMetric(normalizedReportName, "CO2 avoided", formatDecimal(getCo2AvoidedKg()) + " kg"),
                    new CsvMetric(normalizedReportName, "Active listings", String.valueOf(getActiveListings())));
            case "member activity" -> List.of(
                    new CsvMetric(normalizedReportName, "Total borrowed resources",
                            String.valueOf(getTotalBorrowedResources())),
                    new CsvMetric(normalizedReportName, "Recent audit events", String.valueOf(getAuditEvents().size())));
            default -> List.of(
                    new CsvMetric("Usage", "Total shared resources", String.valueOf(getTotalSharedResources())),
                    new CsvMetric("Usage", "Total borrowed resources", String.valueOf(getTotalBorrowedResources())),
                    new CsvMetric("Usage", "Usage rate", getUsageRatePercent() + "%"));
        };

        return "Report,Metric,Value\n"
                + metrics.stream()
                        .map(metric -> csv(metric.report()) + "," + csv(metric.metric()) + "," + csv(metric.value()))
                        .collect(Collectors.joining("\n"));
    }

    public record AuditEvent(String action, String user, String time) {
    }

    private String buildModerationCsv() {
        return "Action,User,Time\n"
                + auditEvents.stream()
                        .map(event -> csv(event.action()) + "," + csv(event.user()) + "," + csv(event.time()))
                        .collect(Collectors.joining("\n"));
    }

    private String formatDecimal(double value) {
        return String.format(Locale.US, "%.1f", value);
    }

    private String csv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private record ResourceRecord(
            String name,
            String category,
            boolean active,
            int borrowCount,
            double savings,
            double co2AvoidedKg) {
    }

    private record CsvMetric(String report, String metric, String value) {
    }
}
