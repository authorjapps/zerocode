package org.jsmart.zerocode.core.domain.reports.cucumber;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CucumberResult {
    private final CucumberStatus status;
    @JsonProperty("error_message")
    private final String errorMessage;
    private final Long duration;

    public CucumberResult(CucumberStatus status, String errorMessage, Long duration) {
        this.status = status;
        this.errorMessage = errorMessage;
        this.duration = duration;
    }

    public CucumberStatus getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Long getDuration() {
        return duration;
    }
}
