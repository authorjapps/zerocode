package org.jsmart.zerocode.core.report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.jsmart.zerocode.core.domain.builders.*;
import org.jsmart.zerocode.core.domain.reports.ZeroCodeExecResult;
import org.jsmart.zerocode.core.domain.reports.ZeroCodeReport;
import org.jsmart.zerocode.core.domain.reports.ZeroCodeReportStep;
import org.jsmart.zerocode.core.domain.reports.chart.HighChartColumnHtml;
import org.jsmart.zerocode.core.domain.reports.csv.ZeroCodeCsvReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.jsmart.zerocode.core.constants.ZeroCodeReportConstants.*;
import static org.jsmart.zerocode.core.domain.builders.ExtentReportsFactory.getReportName;

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

    private final ObjectMapper mapper;

    private List<ZeroCodeReport> treeReports;

    private List<ZeroCodeCsvReport> zeroCodeCsvFlattenedRows;

    private List<ZeroCodeCsvReport> csvRows = new ArrayList<>();

    @Inject
    public ZeroCodeReportGeneratorImpl(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Gets unique steps from a scenario. In case of retries, the steps have same correlation id and if
     * one of the retries is successful, we include it in the result(not the one which failed).
     * In a normal case(without retry), both PASS and FAIL will be included as usual.
     *
     * @param steps
     * @return
     */
    List<ZeroCodeReportStep> getUniqueSteps(List<ZeroCodeReportStep> steps) {
        Map<String, ZeroCodeReportStep> result = new LinkedHashMap<>();
        steps.forEach(step -> {
            result.merge(step.getCorrelationId(), step,
                    (s1, s2) -> RESULT_PASS.equals(s1.getResult()) ? s1 : s2);
        });
        return new ArrayList<>(result.values());
    }

    @Override
    public void generateExtentReport() {

        if (interactiveHtmlReportDisabled) {
            return;
        }

        ExtentReports extentReports = ExtentReportsFactory.createReportTheme(TARGET_FILE_NAME);

        linkToSpikeChartIfEnabled();

        treeReports.forEach(thisReport -> {

            thisReport.getResults().forEach(thisScenario -> {
                ExtentTest test = extentReports.createTest(thisScenario.getScenarioName());

                 /**This code checks if the scenario has meta data.
                 If it does, it iterates through each meta data entry and adds it to
                 the Extent report as an info label.**/
                if (thisScenario.getMeta() != null) {
                    for (Map.Entry<String, List<String>> entry : thisScenario.getMeta().entrySet()) {
                        String key = entry.getKey();
                        List<String> values = entry.getValue();
                        test.info(MarkupHelper.createLabel(key + ": " + String.join(", ", values), ExtentColor.BLUE));
                    }
                }

                // Assign Category
                test.assignCategory(DEFAULT_REGRESSION_CATEGORY); //Super set
                String[] hashTagsArray = optionalCategories(thisScenario.getScenarioName()).toArray(new String[0]);
                if(hashTagsArray.length > 0) {
                    test.assignCategory(hashTagsArray); //Sub categories
                }

                // Assign Authors
                test.assignAuthor(DEFAULT_REGRESSION_AUTHOR); //Super set
                String[] authorsArray = optionalAuthors(thisScenario.getScenarioName()).toArray(new String[0]);
                if(authorsArray.length > 0) {
                    test.assignAuthor(authorsArray); //Sub authors
                }

                List<ZeroCodeReportStep> thisScenarioUniqueSteps = getUniqueSteps(thisScenario.getSteps());
                thisScenarioUniqueSteps.forEach(thisStep -> {
                    test.getModel().setStartTime(utilDateOf(thisStep.getRequestTimeStamp()));
                    test.getModel().setEndTime(utilDateOf(thisStep.getResponseTimeStamp()));

                    final Status testStatus = thisStep.getResult().equals(RESULT_PASS) ? Status.PASS : Status.FAIL;

                    ExtentTest step = test.createNode(thisStep.getName(), TEST_STEP_CORRELATION_ID + " " + thisStep.getCorrelationId());

                    if (testStatus.equals(Status.PASS)) {
                        step.pass(thisStep.getResult());
                    } else {
                        step.info(MarkupHelper.createCodeBlock(thisStep.getOperation() + "\t" + thisStep.getUrl()));
                        step.info(MarkupHelper.createCodeBlock(thisStep.getRequest(), CodeLanguage.JSON));
                        step.info(MarkupHelper.createCodeBlock(thisStep.getResponse(), CodeLanguage.JSON));
                        step.fail(MarkupHelper.createCodeBlock("Reason:\n" + thisStep.getAssertions()));
                    }
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
        if (spikeChartReportEnabled || spikeChartFileName != null) {
            final String reportName = getReportName();

            String linkCodeToTargetSpikeChartHtml =
                    String.format("<code>&nbsp;&nbsp;<a href='%s' style=\"color: #006; background: #ff6;\"> %s </a></code>",
                            spikeChartFileName,
                            LINK_LABEL_NAME);

            ExtentReportsFactory.reportName(reportName + linkCodeToTargetSpikeChartHtml);
        }
    }

    /**
     * @param scenarioName String containing a name of an author
     * @return authors of the test scenario
     */
    protected List<String> optionalAuthors(String scenarioName) {
        return deriveNames(scenarioName, AUTHOR_MARKER_NEW);
    }

    /**
     * @param scenarioName String containing hashtags of a category
     * @return hashtags aka categories of the test scenario
     */
    protected List<String> optionalCategories(String scenarioName) {
        return deriveNames(scenarioName, CATEGORY_MARKER);
    }

    private List<String> deriveNames(String scenarioName, String marker) {
        List<String> nameList = new ArrayList<>();
        for(String thisName : scenarioName.trim().split(" ")){
            if(thisName.startsWith(marker) && !thisName.startsWith(AUTHOR_MARKER_OLD)){
                nameList.add(thisName);
            }
            // Depreciated, but still supports. Remove this via a new ticket
            if(thisName.startsWith(AUTHOR_MARKER_OLD)){
                nameList.add(thisName);
            }
        }
        return nameList;

    }

    protected String onlyScenarioName(String scenarioName) {

        int index = scenarioName.indexOf(AUTHOR_MARKER_OLD);
        if (index == -1) {
            return scenarioName;
        } else {
            return scenarioName.substring(0, index - 1);
        }
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
        LOGGER.debug("####spikeChartReportEnabled: " + spikeChartReportEnabled);

        /*
         * Generate: Spike Chart using HighChart
         */
        if (spikeChartReportEnabled) {
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
                .addColumn("method")
                // This adds new columns to the CSV schema for each type of meta data.
                .addColumn("metaAuthors")
                .addColumn("metaTickets")
                .addColumn("metaCategories")
                .addColumn("metaOthers")
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

                    // Add meta information
                    Map<String, List<String>> meta = thisResult.getMeta();
                    if (meta != null) {
                        /**This code retrieves the meta data from the test result. If meta data exists,
                         * it joins the list of values for each meta data type into a comma-separated
                         * string and adds it to the CSV row.**/
                        csvFileBuilder.setMetaAuthors(String.join(", ", meta.getOrDefault("authors", Collections.emptyList())));
                        csvFileBuilder.setMetaTickets(String.join(", ", meta.getOrDefault("tickets", Collections.emptyList())));
                        csvFileBuilder.setMetaCategories(String.join(", ", meta.getOrDefault("categories", Collections.emptyList())));
                        csvFileBuilder.setMetaOthers(String.join(", ", meta.getOrDefault("others", Collections.emptyList())));
                    }

                    thisResult.getSteps().forEach(thisStep -> {
                        csvFileBuilder.stepLoop(thisStep.getLoop());
                        csvFileBuilder.stepName(thisStep.getName());
                        csvFileBuilder.correlationId(thisStep.getCorrelationId());
                        csvFileBuilder.result(thisStep.getResult());
                        csvFileBuilder.method(thisStep.getOperation());
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

        for (ZeroCodeReport zeroCodeReport : scenarioReports) {
            for (ZeroCodeExecResult zeroCodeExecResult : zeroCodeReport.getResults()) {
                zeroCodeExecResult.setSteps(getUniqueSteps(zeroCodeExecResult.getSteps()));
            }
        }
        return scenarioReports;
    }


    public static List<String> getAllEndPointFilesFrom(String folderName) {

        File[] files = new File(folderName).listFiles((dir, name) -> {
            return name.endsWith(".json");
        });

        if (files == null || files.length == 0) {

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
