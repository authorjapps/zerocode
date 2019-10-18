package org.jsmart.zerocode.core.domain.builders;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jsmart.zerocode.core.domain.reports.cucumber.CucumberResult;
import org.jsmart.zerocode.core.domain.reports.cucumber.CucumberStatus;

public class ZeroCodeCucumberResultBuilder {
    private CucumberStatus status;
    @JsonProperty("error_message")
    private String errorMessage;
    private Long duration;

    public static ZeroCodeCucumberResultBuilder newInstance() {
        return new ZeroCodeCucumberResultBuilder();
    }

    public CucumberResult build() {
        return new CucumberResult(status, errorMessage, duration);
    }

    public ZeroCodeCucumberResultBuilder status(CucumberStatus status) {
        this.status = status;
        return this;
    }

    public ZeroCodeCucumberResultBuilder errorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;

    }

    public ZeroCodeCucumberResultBuilder duration(Long duration) {
        this.duration = duration;
        return this;

    }
}
