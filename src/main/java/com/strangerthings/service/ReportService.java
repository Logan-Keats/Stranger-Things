package com.strangerthings.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.strangerthings.dao.ReportDao;
import com.strangerthings.dao.UserDao;

/** Pure Java reporting logic for Home and Reports screens. */
public class ReportService {
    private static final DateTimeFormatter DATABASE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DISPLAY_TIME = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    private final ReportDao reportDao = new ReportDao();
    private final UserDao userDao = new UserDao();

    public Map<String, Integer> getCategoryDistribution() {
        return reportDao.getCategoryDistribution();
    }

    public Map<String, Integer> getBorrowingActivityByDay() {
        Map<String, Integer> activity = new LinkedHashMap<>();
        reportDao.getBorrowingActivityByDay().forEach((day, count) -> activity.put(formatDay(day), count));
        return activity;
    }

    public int getTotalSharedResources() {
        return reportDao.getTotalSharedCount();
    }

    public int getTotalSharedResources(String username) {
        return reportDao.getTotalSharedCount(username);
    }

    public int getTotalBorrowedResources() {
        return reportDao.getTotalBorrowingCount();
    }

    public int getTotalBorrowedResources(String username) {
        return reportDao.getTotalBorrowingCount(username);
    }

    public int getTotalMembers() {
        return userDao.getMemberCount();
    }

    public int getActiveListings() {
        return reportDao.getActiveListingCount();
    }

    public double getTotalSavings() {
        return reportDao.getTotalSavings();
    }

    public double getTotalSavings(String username) {
        return reportDao.getTotalSavings(username);
    }

    public double getCo2AvoidedKg() {
        return reportDao.getCo2AvoidedKg();
    }

    public double getCo2AvoidedKg(String username) {
        return reportDao.getCo2AvoidedKg(username);
    }

    public int getUsageRatePercent() {
        int sharedResources = getTotalSharedResources();
        return sharedResources == 0 ? 0 : (int) Math.round(reportDao.getCurrentBorrowingCount() * 100.0 / sharedResources);
    }

    public List<AuditEvent> getAuditEvents() {
        return toAuditEvents(reportDao.getAuditEvents());
    }

    public List<AuditEvent> getAuditEvents(String username) {
        return toAuditEvents(reportDao.getAuditEvents(username));
    }

    public Map<String, Integer> getCategoryDistribution(String username) {
        return reportDao.getCategoryDistribution(username);
    }

    private List<AuditEvent> toAuditEvents(List<ReportDao.AuditRecord> events) {
        return events.stream()
                .map(event -> new AuditEvent(event.type(), formatAuditAction(event.action(), event.resourceName()),
                        event.username(), formatTime(event.occurredAt())))
                .toList();
    }

    public List<String> getRecentActivities() {
        return getAuditEvents().stream()
                .map(event -> event.action() + " - " + event.time())
                .toList();
    }

    public List<String> getRecentActivities(String username) {
        return getAuditEvents(username).stream()
                .map(event -> event.action() + " - " + event.time())
                .toList();
    }

    public List<String> getRecentJoinStats() {
        return userDao.findAllMembers().stream()
                .limit(5)
                .map(member -> member.username() + " joined - " + formatTime(member.createdAt()))
                .toList();
    }

    public List<Member> getAllMembers() {
        return userDao.findAllUsers().stream()
                .map(member -> new Member(member.username(), member.role(), formatTime(member.createdAt())))
                .toList();
    }

    public String buildCsv(String reportName) {
        String normalizedReportName = reportName == null || reportName.isBlank() ? "Usage" : reportName.trim();
        if ("Moderation Log".equalsIgnoreCase(normalizedReportName)) {
            return buildModerationCsv();
        }
        List<CsvMetric> metrics = switch (normalizedReportName.toLowerCase(Locale.ROOT)) {
            case "co2 avoided" -> List.of(
                    new CsvMetric(normalizedReportName, "CO2 avoided", formatDecimal(getCo2AvoidedKg()) + " kg"),
                    new CsvMetric(normalizedReportName, "Money saved", "$" + formatDecimal(getTotalSavings())));
            case "member activity" -> List.of();
            default -> List.of(
                    new CsvMetric("Usage", "Total shared resources", String.valueOf(getTotalSharedResources())),
                    new CsvMetric("Usage", "Total borrowings", String.valueOf(getTotalBorrowedResources())),
                    new CsvMetric("Usage", "Usage rate", getUsageRatePercent() + "%"));
        };
        if ("Member Activity".equalsIgnoreCase(normalizedReportName)) {
            return buildMemberActivityCsv();
        }
        return "Report,Metric,Value\n" + metrics.stream()
                .map(metric -> csv(metric.report()) + "," + csv(metric.metric()) + "," + csv(metric.value()))
                .collect(Collectors.joining("\n"));
    }

    public record AuditEvent(String type, String action, String user, String time) {
    }

    public record Member(String username, String role, String joinedAt) {
    }

    private String buildModerationCsv() {
        return "Type,Action,User,Time\n" + getAuditEvents().stream()
                .map(event -> csv(event.type()) + "," + csv(event.action()) + "," + csv(event.user()) + "," + csv(event.time()))
                .collect(Collectors.joining("\n"));
    }

    private String buildMemberActivityCsv() {
        return "Username,Role,Resources shared,Bookings,Audit activities\n"
                + userDao.findUserActivities().stream()
                        .map(activity -> csv(activity.username()) + "," + csv(activity.role()) + ","
                                + activity.resourceCount() + "," + activity.bookingCount() + ","
                                + activity.activityCount())
                        .collect(Collectors.joining("\n"));
    }

    private String formatAuditAction(String action, String resourceName) {
        return resourceName == null || resourceName.isBlank() ? action : action + ": " + resourceName;
    }

    private String formatDay(String value) {
        try {
            return LocalDateTime.parse(value + " 00:00:00", DATABASE_TIME).format(DateTimeFormatter.ofPattern("d/M"));
        } catch (DateTimeParseException exception) {
            return value;
        }
    }

    private String formatTime(String value) {
        try {
            return LocalDateTime.parse(value, DATABASE_TIME).format(DISPLAY_TIME);
        } catch (DateTimeParseException exception) {
            return value;
        }
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

    private record CsvMetric(String report, String metric, String value) {
    }
}
