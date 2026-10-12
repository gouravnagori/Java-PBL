package com.legal.dto;

/**
 * Data transfer object representing severity distribution breakdown.
 * Authored by Harshvardhan Bhatt (Reports & Analytics Module).
 */
public class RiskDistributionResponse {

    private long lowCount;
    private long mediumCount;
    private long highCount;
    private long criticalCount;
    private double overallRiskScore;

    public RiskDistributionResponse() {}

    public RiskDistributionResponse(long lowCount, long mediumCount, long highCount, long criticalCount, double overallRiskScore) {
        this.lowCount = lowCount;
        this.mediumCount = mediumCount;
        this.highCount = highCount;
        this.criticalCount = criticalCount;
        this.overallRiskScore = overallRiskScore;
    }

    public long getLowCount() {
        return lowCount;
    }

    public void setLowCount(long lowCount) {
        this.lowCount = lowCount;
    }

    public long getMediumCount() {
        return mediumCount;
    }

    public void setMediumCount(long mediumCount) {
        this.mediumCount = mediumCount;
    }

    public long getHighCount() {
        return highCount;
    }

    public void setHighCount(long highCount) {
        this.highCount = highCount;
    }

    public long getCriticalCount() {
        return criticalCount;
    }

    public void setCriticalCount(long criticalCount) {
        this.criticalCount = criticalCount;
    }

    public double getOverallRiskScore() {
        return overallRiskScore;
    }

    public void setOverallRiskScore(double overallRiskScore) {
        this.overallRiskScore = overallRiskScore;
    }
}
