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
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.AUTHOR_MARKER;
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.RESULT_PASS;
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.TARGET_FILE_NAME;
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.TARGET_FULL_REPORT_CSV_FILE_NAME;
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.TARGET_FULL_REPORT_DIR;
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.TARGET_REPORT_DIR;

public class ZeroCodeReportGeneratorImpl implements ZeroCodeReportGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZeroCodeReportGeneratorImpl.class);

    /**
     * Spike chat is disabled by default
     */
    @Inject(optional = true)
    @Named("report.spike.chart.enabled")
    private boolean spikeChartReportEnabled;

    @Inject
    private ObjectMapper mapper;

    private List<ZeroCodeReport> treeReports;

    private List<ZeroCodeCsvReport> zeroCodeCsvFlattenedRows;

    private List<ZeroCodeCsvReport> csvRows = new ArrayList<>();

    public ZeroCodeReportGeneratorImpl() {
    }

    @Override
    public void generateExtentReport() {

        ExtentReports extentReports = ExtentReportsFactory.createReportTheme(TARGET_FILE_NAME);

        treeReports.forEach(thisReport -> {

            thisReport.getResults().forEach(thisScenario -> {
                ExtentTest test = extentReports.createTest(thisScenario.getScenarioName());
                test.assignCategory("Regression"); //Read this from POM file //TODO

                test.assignAuthor(optionalAuthor(thisScenario.getScenarioName()));

                thisScenario.getSteps().forEach(thisStep -> {
                    test.getModel().setStartTime(utilDateOf(thisStep.getRequestTimeStamp()));
                    test.getModel().setEndTime(utilDateOf(thisStep.getResponseTimeStamp()));

                    final Status testStatus = thisStep.getResult().equals(RESULT_PASS) ? Status.PASS : Status.FAIL;
                    test.createNode(thisStep.getName(), "CORRELATION-ID: "
                            + thisStep.getCorrelationId()).log(testStatus, thisStep.getName()
                            + " has " + thisStep.getResult()
                            + ". \n Search in the log file for-  CORRELATION-ID:  \n"
                            + thisStep.getCorrelationId() + "\n"
                            + ", url:" + thisStep.getUrl() + "\n"
                    );

                    extentReports.flush();
                });

            });

        });
    }

    protected String optionalAuthor(String scenarioName) {
        String authorName = substringBetween(scenarioName, AUTHOR_MARKER, AUTHOR_MARKER);

        if(authorName == null){
            authorName = substringBetween(scenarioName, AUTHOR_MARKER, " ");
        }

        if(authorName == null){
            authorName = scenarioName.substring(scenarioName.lastIndexOf(AUTHOR_MARKER) + 1);
        }

        if(scenarioName.lastIndexOf(AUTHOR_MARKER) == -1 || authorName == null){
            authorName = "Unknown-Author";
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
         * Generate: Chart using HighChart
         */
        if(spikeChartReportEnabled){
            HighChartColumnHtml highChartColumnHtml = convertCsvRowsToHighChartData(zeroCodeCsvFlattenedRows);
            generateHighChartReport(highChartColumnHtml);
        }
    }


    private HighChartColumnHtml convertCsvRowsToHighChartData(List<ZeroCodeCsvReport> zeroCodeCsvReportRows) {

        //TODO: read from the property file. Inject as fields.
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

        highChartColumnHtmlWriter.generateHighChart(highChartColumnHtml);
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
                .addColumn("responseTimeStamp")
                .addColumn("responseDelayMilliSec", CsvSchema.ColumnType.NUMBER)
                .addColumn("result")
                .build();

        CsvMapper csvMapper = new CsvMapper();
        csvMapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);

        ObjectWriter writer = csvMapper.writer(schema.withLineSeparator("\n"));
        try {
            writer.writeValue(
                    new File(TARGET_FULL_REPORT_DIR +
                            TARGET_FULL_REPORT_CSV_FILE_NAME +
                            "_" +
                            LocalDateTime.now().toString().replace(":", "-") +
                            ".csv"),
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
                        csvFileBuilder.correlationId(thisStep.getCorrelationId()); //<-- in case of searching in the log file
                        csvFileBuilder.result(thisStep.getResult()); //<-- passed or failed
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

        final List<String> fileNames = ofNullable(Arrays.asList(files)).orElse(emptyList()).stream()
                .map(thisFile -> thisFile.getAbsolutePath())
                .collect(Collectors.toList());

        return fileNames;
    }

    protected void validateReportsFolderAndTheFilesExists(String reportsFolder) {

        try {
            File[] files = new File(reportsFolder).listFiles((dir, fileName) -> fileName.endsWith(".json"));

            ofNullable(files).orElseThrow(() -> new RuntimeException("Somehow the '" + reportsFolder + "' has got no files."));

        } catch (Exception e) {
            e.printStackTrace();
            final String message = "\n----------------------------------------------------------------------------------------\n" +
                    "Somehow the '" + reportsFolder + "' is not present or has no report JSON files. \n" +
                    "Possible reasons- \n" +
                    "   1) No tests were activated or made to run via ZeroCode runner. -or- \n" +
                    "   2) You have simply used @RunWith(...) and ignored all tests -or- \n" +
                    "   3) Permission issue to create/write folder/files \n" +
                    "   4) Please fix it by adding/activating at least one test case or fix the file permission issue\n" +
                    "----------------------------------------------------------------------------------------\n";

            throw new RuntimeException(message);
        }

    }

    private static Date utilDateOf(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
