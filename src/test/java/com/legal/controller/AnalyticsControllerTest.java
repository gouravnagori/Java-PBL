package com.legal.controller;

import com.legal.dto.DashboardMetricsResponse;
import com.legal.service.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Controller unit test suite for Analytics endpoints.
 * Authored by Harshvardhan Bhatt (Reports & Analytics Module).
 */
class AnalyticsControllerTest {

    @Mock
    private AnalyticsService analyticsService;

    private AnalyticsController analyticsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        analyticsController = new AnalyticsController(analyticsService);
    }

    @Test
    void testGetDashboardMetrics_ReturnsSuccess() {
        DashboardMetricsResponse metrics = new DashboardMetricsResponse();
        metrics.setTotalDocumentsProcessed(5);
        when(analyticsService.getGlobalDashboardMetrics()).thenReturn(metrics);

        ResponseEntity<?> response = analyticsController.getDashboardMetrics(null);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(analyticsService, times(1)).getGlobalDashboardMetrics();
    }
}
