package org.jsmart.zerocode.core.constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static org.jsmart.zerocode.core.constants.ZeroCodeReportConstants.REPORT_STEP_CAPTURE_KEY;

/**
 * Decides which steps get their request and response written to the JSON report.
 * Set via zerocode.report.step.capture in zerocode.properties.
 * e.g.
 * -  FAILED_ONLY (default) -> only FAILED steps carry request/response
 * -  ALL                   -> PASSED steps carry them too
 */
public enum ReportStepCaptureMode {
    FAILED_ONLY,
    ALL;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportStepCaptureMode.class);

    /**
     * Parses a property value, trimmed and case-insensitive. Missing, blank or unknown values fall back to
     * FAILED_ONLY, so a typo never breaks a test run.
     */
    public static ReportStepCaptureMode fromProperty(String propertyValue) {
        if (propertyValue == null || propertyValue.trim().isEmpty()) {
            return FAILED_ONLY;
        }
        String normalisedValue = propertyValue.trim().toUpperCase();
        for (ReportStepCaptureMode captureMode : values()) {
            if (captureMode.name().equals(normalisedValue)) {
                return captureMode;
            }
        }
        LOGGER.warn("Unknown {}='{}'. Allowed: {}. Using FAILED_ONLY.",
                REPORT_STEP_CAPTURE_KEY, propertyValue, Arrays.toString(values()));
        return FAILED_ONLY;
    }

    /**
     * Tells whether a step's request and response go into the report.
     * FAILED steps always do. PASSED steps only in ALL mode.
     */
    public boolean shouldCapture(boolean isStepPassed) {
        return this == ALL || !isStepPassed;
    }
}
