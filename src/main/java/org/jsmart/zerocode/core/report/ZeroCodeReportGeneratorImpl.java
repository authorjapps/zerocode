package org.jsmart.zerocode.core.report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.lang.StringUtils;
import org.jsmart.zerocode.core.domain.builders.ExtentReportsFactory;
import org.jsmart.zerocode.core.domain.builders.HighChartColumnHtmlBuilder;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeChartKeyValueArrayBuilder;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeChartKeyValueBuilder;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeCsvReportBuilder;
import org.jsmart.zerocode.core.domain.reports.ZeroCodeReport;
import org.jsmart.zerocode.core.domain.reports.chart.HighChartColumnHtml;
import org.jsmart.zerocode.core.domain.reports.csv.ZeroCodeCsvReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang.StringUtils.substringBetween;
import static org.jsmart.zerocode.core.domain.builders.ExtentReportsFactory.getReportName;
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.ANONYMOUS_AUTHOR;
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.AUTHOR_MARKER;
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.DEFAULT_REGRESSION_CATEGORY;
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.HIGH_CHART_HTML_FILE_NAME;
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.LINK_LABEL_NAME;
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.RESULT_PASS;
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.TARGET_FILE_NAME;
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.TARGET_FULL_REPORT_CSV_FILE_NAME;
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.TARGET_FULL_REPORT_DIR;
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.TARGET_REPORT_DIR;
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.TEST_STEP_CORRELATION_ID;

public class ZeroCodeReportGeneratorImpl implements ZeroCodeReportGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZeroCodeReportGeneratorImpl.class);

    private static String spikeChartFileName;

    /**
     * Spike chat is disabled by default
     */
    @Inject(optional = true)
    @Named("report.spike.chart.enabled")
    private boolean spikeChartReportEnabled;

    /**
     * Spike chat is disabled by default
     */
    @Inject(optional = true)
    @Named("interactive.html.report.disabled")
    private boolean interactiveHtmlReportDisabled;

    @Inject
    private ObjectMapper mapper;

    private List<ZeroCodeReport> treeReports;

    private List<ZeroCodeCsvReport> zeroCodeCsvFlattenedRows;

    private List<ZeroCodeCsvReport> csvRows = new ArrayList<>();

    public ZeroCodeReportGeneratorImpl() {
    }

    @Override
    public void generateExtentReport() {

        if(interactiveHtmlReportDisabled){
            return;
        }

        ExtentReports extentReports = ExtentReportsFactory.createReportTheme(TARGET_FILE_NAME);

        linkToSpikeChartIfEnabled();

        treeReports.forEach(thisReport -> {

            thisReport.getResults().forEach(thisScenario -> {
                ExtentTest test = extentReports.createTest(thisScenario.getScenarioName());
                test.assignCategory(DEFAULT_REGRESSION_CATEGORY);

                test.assignAuthor(optionalAuthor(thisScenario.getScenarioName()));

                thisScenario.getSteps().forEach(thisStep -> {
                    test.getModel().setStartTime(utilDateOf(thisStep.getRequestTimeStamp()));
                    test.getModel().setEndTime(utilDateOf(thisStep.getResponseTimeStamp()));

                    final Status testStatus = thisStep.getResult().equals(RESULT_PASS) ? Status.PASS : Status.FAIL;
                    test.createNode(thisStep.getName(), TEST_STEP_CORRELATION_ID + " "
                            + thisStep.getCorrelationId()).log(testStatus, thisStep.getName()
                            + " has " + thisStep.getResult()
                            + ". \n Search in the log file for-  " + TEST_STEP_CORRELATION_ID + "  \n"
                            + thisStep.getCorrelationId() + "\n"
                            + ", url:" + thisStep.getUrl() + "\n"
                    );

                    extentReports.flush();
                });

            });

        });
    }

    public void linkToSpikeChartIfEnabled() {

        // ------------------------------------------------
        // If spikeChartFileName is not null,
        // that means it was enabled by one of the runner.
        // (might be disabled by current runner)
        // Then it's good to link it to that spike report.
        // ------------------------------------------------
        if(spikeChartReportEnabled || spikeChartFileName != null){
            final String reportName = getReportName();

            String linkCodeToTargetSpikeChartHtml =
                    String.format("<code>&nbsp;&nbsp;<a href='%s' style=\"color: #006; background: #ff6;\"> %s </a></code>",
                    spikeChartFileName,
                    LINK_LABEL_NAME);;

            ExtentReportsFactory.reportName(reportName + linkCodeToTargetSpikeChartHtml);
        }
    }

    protected String optionalAuthor(String scenarioName) {
        String authorName = substringBetween(scenarioName, AUTHOR_MARKER, AUTHOR_MARKER);

        if(authorName == null){
            authorName = substringBetween(scenarioName, AUTHOR_MARKER, ",");
        }

        if(authorName == null){
            authorName = substringBetween(scenarioName, AUTHOR_MARKER, " ");
        }

        if(authorName == null){
            authorName = scenarioName.substring(scenarioName.lastIndexOf(AUTHOR_MARKER) + AUTHOR_MARKER.length());
        }

        if(scenarioName.lastIndexOf(AUTHOR_MARKER) == -1 || StringUtils.isEmpty(authorName)){
            authorName = ANONYMOUS_AUTHOR;
        }

        return authorName;
    }

    @Override
    public void generateCsvReport() {
        /*
         * Read individual reports for aggregation
         */
        treeReports = readZeroCodeReportsByPath(TARGET_REPORT_DIR);

        /*
         * Generate: CSV report
         */
        zeroCodeCsvFlattenedRows = buildCsvRows();
        generateCsvReport(zeroCodeCsvFlattenedRows);
    }

    @Override
    public void generateHighChartReport() {
        LOGGER.info("####spikeChartReportEnabled: " + spikeChartReportEnabled);

        /*
         * Generate: Spike Chart using HighChart
         */
        if(spikeChartReportEnabled){
            HighChartColumnHtml highChartColumnHtml = convertCsvRowsToHighChartData(zeroCodeCsvFlattenedRows);
            generateHighChartReport(highChartColumnHtml);
        }
    }


    private HighChartColumnHtml convertCsvRowsToHighChartData(List<ZeroCodeCsvReport> zeroCodeCsvReportRows) {

        HighChartColumnHtmlBuilder highChartColumnHtmlBuilder = HighChartColumnHtmlBuilder.newInstance()
                .chartSeriesName("Test Results")
                .chartTitleTop("Request Vs Response Delay Chart")
                .textYaxis("Response Delay in Milli Sec")
                .chartTitleTopInABox("Spike Chart ( Milli Seconds )");

        ZeroCodeChartKeyValueArrayBuilder dataArrayBuilder = ZeroCodeChartKeyValueArrayBuilder.newInstance();

        zeroCodeCsvReportRows.forEach(thisRow ->
                dataArrayBuilder.kv(ZeroCodeChartKeyValueBuilder.newInstance()
                        .key(thisRow.getScenarioName() + "->" + thisRow.getStepName())
                        .value(thisRow.getResponseDelayMilliSec())
                        .result(thisRow.getResult())
                        .build())
        );

        highChartColumnHtmlBuilder.testResult(dataArrayBuilder.build());

        return highChartColumnHtmlBuilder.build();

    }

    public void generateHighChartReport(HighChartColumnHtml highChartColumnHtml) {

        HighChartColumnHtmlWriter highChartColumnHtmlWriter = new HighChartColumnHtmlWriter();

        spikeChartFileName = createTimeStampedFileName();

        highChartColumnHtmlWriter.generateHighChart(highChartColumnHtml, spikeChartFileName);
    }

    public void generateCsvReport(List<ZeroCodeCsvReport> zeroCodeCsvReportRows) {

        /*
         * Write to a CSV file
         */
        CsvSchema schema = CsvSchema.builder()
                .setUseHeader(true)
                .addColumn("scenarioName")
                .addColumn("scenarioLoop", CsvSchema.ColumnType.NUMBER)
                .addColumn("stepName")
                .addColumn("stepLoop", CsvSchema.ColumnType.NUMBER)
                .addColumn("correlationId")
                .addColumn("requestTimeStamp")
                .addColumn("responseDelayMilliSec", CsvSchema.ColumnType.NUMBER)
                .addColumn("responseTimeStamp")
                .addColumn("result")
                .build();

        CsvMapper csvMapper = new CsvMapper();
        csvMapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);

        ObjectWriter writer = csvMapper.writer(schema.withLineSeparator("\n"));
        try {
            writer.writeValue(
                    new File(TARGET_FULL_REPORT_DIR +
                            TARGET_FULL_REPORT_CSV_FILE_NAME
                            //"_" +
                            //LocalDateTime.now().toString().replace(":", "-") +
                            //".csv"
                    ),
                    zeroCodeCsvReportRows);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Exception while Writing full CSV report. Details: " + e);
        }
    }

    public List<ZeroCodeCsvReport> buildCsvRows() {
        /*
         * Map the java list to CsvPojo
         */
        ZeroCodeCsvReportBuilder csvFileBuilder = ZeroCodeCsvReportBuilder.newInstance();

        treeReports.forEach(thisReport ->
                thisReport.getResults().forEach(thisResult -> {

                    csvFileBuilder.scenarioLoop(thisResult.getLoop());
                    csvFileBuilder.scenarioName(thisResult.getScenarioName());

                    thisResult.getSteps().forEach(thisStep -> {
                        csvFileBuilder.stepLoop(thisStep.getLoop());
                        csvFileBuilder.stepName(thisStep.getName());
                        csvFileBuilder.correlationId(thisStep.getCorrelationId());
                        csvFileBuilder.result(thisStep.getResult());
                        csvFileBuilder.requestTimeStamp(thisStep.getRequestTimeStamp().toString());
                        csvFileBuilder.responseTimeStamp(thisStep.getResponseTimeStamp().toString());
                        csvFileBuilder.responseDelayMilliSec(thisStep.getResponseDelay());

                        /*
                         * Add one by one row
                         */
                        csvRows.add(csvFileBuilder.build());

                    });
                })
        );

        return csvRows;
    }

    public List<ZeroCodeReport> readZeroCodeReportsByPath(String reportsFolder) {

        validateReportsFolderAndTheFilesExists(reportsFolder);

        List<String> allEndPointFiles = getAllEndPointFilesFrom(reportsFolder);

        List<ZeroCodeReport> scenarioReports = allEndPointFiles.stream()
                .map(reportJsonFile -> {
                    try {

                        return mapper.readValue(new File(reportJsonFile), ZeroCodeReport.class);

                    } catch (IOException e) {
                        e.printStackTrace();

                        throw new RuntimeException("Exception while deserializing to ZeroCodeReport. Details: " + e);

                    }
                })
                .collect(Collectors.toList());

        return scenarioReports;
    }


    public static List<String> getAllEndPointFilesFrom(String folderName) {

        File[] files = new File(folderName).listFiles((dir, name) -> {
            return name.endsWith(".json");
        });

        if(files == null || files.length == 0){

            LOGGER.error("\n\t\t\t************\nNow files were found in folder:{}, hence could not proceed. " +
                    "\n(If this was intentional, then you can safely ignore this error)" +
                    " \n\t\t\t************** \n\n", folderName);
            return emptyList();

        } else {
            return ofNullable(Arrays.asList(files)).orElse(emptyList()).stream()
                    .map(thisFile -> thisFile.getAbsolutePath())
                    .collect(Collectors.toList());
        }
    }

    protected void validateReportsFolderAndTheFilesExists(String reportsFolder) {

        try {
            File[] files = new File(reportsFolder).listFiles((dir, fileName) -> fileName.endsWith(".json"));

            ofNullable(files).orElseThrow(() -> new RuntimeException("Somehow the '" + reportsFolder + "' has got no files."));

        } catch (Exception e) {
            final String message = "\n----------------------------------------------------------------------------------------\n" +
                    "Somehow the '" + reportsFolder + "' is not present or has no report JSON files. \n" +
                    "Possible reasons- \n" +
                    "   1) No tests were activated or made to run via ZeroCode runner. -or- \n" +
                    "   2) You have simply used @RunWith(...) and ignored all tests -or- \n" +
                    "   3) Permission issue to create/write folder/files \n" +
                    "   4) Please fix it by adding/activating at least one test case or fix the file permission issue\n" +
                    "   5) If you are not concerned about reports, you can safely ignore this\n" +
                    "----------------------------------------------------------------------------------------\n";

            // TODO- Can Suppress as this error as this is only related to report. It doesn't hurt or affect the tests at all.
            throw new RuntimeException(message + e);
        }

    }

    private static Date utilDateOf(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private String createTimeStampedFileName() {
        return HIGH_CHART_HTML_FILE_NAME +
                LocalDateTime.now().toString().replace(":", "-") +
                ".html";
    }

}
