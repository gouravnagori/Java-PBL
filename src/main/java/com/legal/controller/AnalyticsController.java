package com.legal.controller;

import com.legal.dto.ApiResponse;
import com.legal.dto.DashboardMetricsResponse;
import com.legal.dto.RiskDistributionResponse;
import com.legal.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller exposing analytics dashboard endpoints and risk metrics summaries.
 * Authored by Harshvardhan Bhatt (Reports & Analytics Module).
 */
@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Autowired
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardMetricsResponse>> getDashboardMetrics(
            @RequestParam(required = false) String userId) {
        DashboardMetricsResponse metrics = (userId != null && !userId.isBlank())
                ? analyticsService.getUserDashboardMetrics(userId)
                : analyticsService.getGlobalDashboardMetrics();

        return ResponseEntity.ok(ApiResponse.success("Dashboard analytics retrieved successfully", metrics));
    }

    @GetMapping("/risk-distribution")
    public ResponseEntity<ApiResponse<RiskDistributionResponse>> getRiskDistribution(
            @RequestParam(required = false) String userId) {
        RiskDistributionResponse distribution = analyticsService.getRiskDistribution(userId);
        return ResponseEntity.ok(ApiResponse.success("Risk distribution statistics retrieved successfully", distribution));
    }
}
