package com.legal.model;

/**
 * Lifecycle status of a legal document within the processing pipeline.
 * Transitions: UPLOADED -> PROCESSING -> PROCESSED (or FAILED)
 */
public enum DocumentStatus {
    UPLOADED,
    PROCESSING,
    PROCESSED,
    FAILED
}
