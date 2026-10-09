package com.legal.model.enums;

/**
 * Risk severity levels used by the Risk Detection Engine.
 * Each level maps to a weighted score contribution to the composite risk index.
 * Owned by Dilip Kumawat — Legal Analysis & Risk Detection Module.
 *
 * F7 / FR9 — Risk Detection requirement.
 */
public enum Severity {

    /**
     * LOW (score weight: 5) — Informational note; standard boilerplate clause.
     * No immediate action required.
     */
    LOW(5),

    /**
     * MEDIUM (score weight: 15) — Ambiguous timeline, non-standard notice period,
     * or mildly one-sided obligation.
     */
    MEDIUM(15),

    /**
     * HIGH (score weight: 30) — One-sided indemnification, broad non-compete scope,
     * missing dispute resolution clause.
     */
    HIGH(30),

    /**
     * CRITICAL (score weight: 50) — Unlimited liability, unilateral termination
     * without cause, waiver of fundamental rights.
     */
    CRITICAL(50);

    /** Numeric weight applied during composite risk score computation. */
    private final int weight;

    Severity(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
