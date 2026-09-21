package com.strangerthings.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * TDD tests for report calculations. These intentionally fail while
 * {@link ReportService} is still a stub.
 */
public class ReportServiceTest {

    private ReportService reportService;

    @BeforeEach
    void setUp() {
        reportService = new ReportService();
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
