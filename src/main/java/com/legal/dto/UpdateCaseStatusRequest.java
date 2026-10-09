package com.legal.dto;

import com.legal.model.CaseStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateCaseStatusRequest {

    @NotNull(message = "Status cannot be null")
    private CaseStatus status;

    public UpdateCaseStatusRequest() {}

    public CaseStatus getStatus() { return status; }
    public void setStatus(CaseStatus status) { this.status = status; }
}
