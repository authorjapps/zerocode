package org.jsmart.zerocode.core.domain.reports;

public interface ZeroCodeReportProperties {
    String RESULT_PASS = "PASSED";
    String RESULT_FAIL = "FAILED";
    String TEST_STEP_CORRELATION_ID = "TEST-STEP-CORRELATION-ID:";
    String TARGET_FULL_REPORT_DIR = "target/";
    String TARGET_REPORT_DIR = "target/zerocode-test-reports/";
    String TARGET_FULL_REPORT_CSV_FILE_NAME = "zerocode-junit-granular-report.csv";
    String TARGET_FILE_NAME = "target/zerocode-junit-interactive-fuzzy-search.html";
    String HIGH_CHART_HTML_FILE_NAME = "zerocode_results_chart";
    String AUTHOR_MARKER = "@@";
    String ANONYMOUS_AUTHOR = "Anonymous";
    String REPORT_TITLE_DEFAULT = "Zerocode Test Report";
    String REPORT_DISPLAY_NAME_DEFAULT = "Zerocode Interactive Report";
    String DEFAULT_REGRESSION_CATEGORY = "Regression";
    String LINK_LABEL_NAME = "Spike Chart(Click here)";

}
