package com.legal.model.enums;

/**
 * Lifecycle states of a legal document analysis pipeline run.
 * Owned by Dilip Kumawat — Legal Analysis & Risk Detection Module.
 */
public enum AnalysisStatus {

    /** Analysis job has been accepted and is waiting to be processed. */
    PENDING,

    /** Clause segmentation and risk scoring are actively running. */
    ANALYSING,

    /** All clauses detected, risk flags computed, composite score persisted. */
    COMPLETED,

    /** Analysis pipeline terminated abnormally; inspect errorMessage for details. */
    FAILED
}
