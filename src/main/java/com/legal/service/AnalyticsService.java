package com.legal.service;

import com.legal.dto.DashboardMetricsResponse;
import com.legal.dto.RiskDistributionResponse;

/**
 * Service interface for computing global and user analytics metrics.
 * Authored by Harshvardhan Bhatt (Reports & Analytics Module).
 */
public interface AnalyticsService {

    /**
     * Retrieve system-wide dashboard metrics.
     */
    DashboardMetricsResponse getGlobalDashboardMetrics();

    /**
     * Retrieve user-specific dashboard metrics.
     */
    DashboardMetricsResponse getUserDashboardMetrics(String userId);

    /**
     * Compute risk severity distribution statistics.
     */
    RiskDistributionResponse getRiskDistribution(String userId);
}
