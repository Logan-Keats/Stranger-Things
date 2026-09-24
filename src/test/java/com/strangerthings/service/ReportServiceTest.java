package com.strangerthings.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.strangerthings.db.DatabaseInitializer;
import com.strangerthings.db.SqliteConnection;

/**
 * TDD tests for report calculations. These intentionally fail while
 * {@link ReportService} is still a stub.
 */
public class ReportServiceTest {

    private ReportService reportService;
    private Connection anchorConnection;

    @BeforeEach
    void setUp() throws SQLException {
        System.setProperty("strangerthings.db.url", "jdbc:sqlite:file:report-service-test?mode=memory&cache=shared");
        anchorConnection = SqliteConnection.getInstance();
        DatabaseInitializer.initialize();
        reportService = new ReportService();
    }

    @AfterEach
    void tearDown() throws SQLException {
        anchorConnection.close();
        System.clearProperty("strangerthings.db.url");
    }

    @Test
    void getCategoryDistribution_countsResourcesByCategory() {
        Map<String, Integer> distribution = reportService.getCategoryDistribution();

        assertEquals(1, distribution.get("Tools"));
        assertEquals(1, distribution.get("Garden Equipment"));
        assertEquals(1, distribution.get("Electronics"));
    }

    @Test
    void getTotalSavings_sumsCompletedBorrowSavings() {
        assertEquals(132.50, reportService.getTotalSavings(), 0.001);
    }

    @Test
    void getCo2AvoidedKg_sumsEnvironmentalImpactForSharedResources() {
        assertEquals(18.5, reportService.getCo2AvoidedKg(), 0.001);
    }

    @Test
    void buildCsv_includesHeaderAndRowsForUsageExport() {
        String csv = reportService.buildCsv("Usage");

        assertTrue(csv.startsWith("Report,Metric,Value"));
        assertTrue(csv.contains("Usage,Total shared resources,3"));
        assertTrue(csv.contains("Usage,Usage rate,67%"));
    }
}
