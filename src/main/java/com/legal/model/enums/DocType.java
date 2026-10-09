package com.legal.model.enums;

/**
 * Enumeration of recognised legal document categories.
 * Used by ClassificationService (Dilip Kumawat — Legal Analysis & Risk Detection Module).
 *
 * F4 / FR6 — Document Classification requirement.
 */
public enum DocType {

    /** Non-Disclosure / Confidentiality Agreement */
    NDA,

    /** Employment / Appointment Contract */
    EMPLOYMENT,

    /** Rental / Lease Agreement */
    RENTAL,

    /** Loan / Credit Agreement */
    LOAN,

    /** Service Level / Professional Services Agreement */
    SERVICE_AGREEMENT,

    /** Partnership / Joint Venture Agreement */
    PARTNERSHIP,

    /** Software / SaaS Subscription Agreement */
    SAAS,

    /** General Purchase / Sale Agreement */
    PURCHASE,

    /** Settlement / Release Agreement */
    SETTLEMENT,

    /** General / Unknown document type (fallback) */
    GENERAL
}
