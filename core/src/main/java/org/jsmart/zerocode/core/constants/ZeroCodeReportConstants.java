package org.jsmart.zerocode.core.constants;

public interface ZeroCodeReportConstants {
    String RESULT_PASS = "PASSED";
    String RESULT_FAIL = "FAILED";
    String TEST_STEP_CORRELATION_ID = "TEST-STEP-CORRELATION-ID:";
    String TARGET_FULL_REPORT_DIR = "target/";
    String TARGET_REPORT_DIR = "target/zerocode-test-reports/";
    String TARGET_FULL_REPORT_CSV_FILE_NAME = "zerocode-junit-granular-report.csv";
    String TARGET_FILE_NAME = "target/zerocode-junit-interactive-fuzzy-search.html";
    String HIGH_CHART_HTML_FILE_NAME = "zerocode_results_chart";
    String AUTHOR_MARKER_OLD = "@@"; //Deprecated
    String AUTHOR_MARKER_NEW = "@";
    String CATEGORY_MARKER = "#";
    String ANONYMOUS_CAT = "Anonymous";
    String REPORT_TITLE_DEFAULT = "Zerocode Test Report";
    String REPORT_DISPLAY_NAME_DEFAULT = "Zerocode Interactive Report";
    String DEFAULT_REGRESSION_CATEGORY = "Regression";
    String DEFAULT_REGRESSION_AUTHOR = "All";
    String LINK_LABEL_NAME = "Spike Chart(Click here)";
    String ZEROCODE_JUNIT = "zerocode.junit";
    String CHARTS_AND_CSV = "gen-smart-charts-csv-reports";

    // Custom js and css for extent report
    String EXTENT_ADDITIONAL_JS = "document.querySelector('.vheader').insertAdjacentHTML('afterbegin'," +
            "'<div id=\"theme-selector\"class=\"nav-right\"onClick=$(\"body\").toggleClass(\"dark\")>" +
            "<span class=\"badge badge-primary\"><i class=\"fa fa-desktop\"></i></span></div>')";
    String EXTENT_ADDITIONAL_CSS = "#theme-selector{padding-right:12px;padding-left:12px;margin-right:10px}";
}
