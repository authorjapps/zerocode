package org.jsmart.zerocode.core.engine.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.inject.Inject;
import org.jsmart.zerocode.core.domain.reports.builders.HighChartColumnHtmlBuilder;
import org.jsmart.zerocode.core.domain.reports.builders.ZeroCodeChartKeyValueArrayBuilder;
import org.jsmart.zerocode.core.domain.reports.builders.ZeroCodeChartKeyValueBuilder;
import org.jsmart.zerocode.core.domain.reports.chart.HighChartColumnHtml;
import org.jsmart.zerocode.core.domain.reports.chart.ZeroCodeChartKeyValue;
import org.jsmart.zerocode.core.domain.reports.csv.ZeroCodeCsvReport;
import org.jsmart.zerocode.core.domain.reports.ZeroCodeReport;
import org.jsmart.zerocode.core.domain.reports.builders.ZeroCodeCsvReportBuilder;
import org.jsmart.zerocode.core.report.HighChartColumnHtmlWriter;
import org.jsmart.zerocode.core.runner.ZeroCodeMultiStepsScenarioRunnerImpl;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.TARGET_FULL_REPORT_CSV_FILE_NAME;
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.TARGET_FULL_REPORT_DIR;
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.TARGET_REPORT_DIR;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Siddha on 24-jul-2016
 */
public class ZeroCodeTestListener extends RunListener {
    private static final org.slf4j.Logger LOGGER = getLogger(ZeroCodeTestListener.class);

    private final ObjectMapper mapper;

    private List<ZeroCodeReport> individualReports;

    List<ZeroCodeCsvReport> csvRows = new ArrayList<>();

    @Inject
    public ZeroCodeTestListener(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
        /*
         * Called before any tests have been run.
         * -Do nothing for time being-
         */
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        /*
         * Called when all tests have finished
         */
        LOGGER.info("### ZeroCode: all testRunFinished. Generating test reports and charts");

        generateCharts();

    }

    private void generateCharts() {
        //TODO: use CoR pattern.

        /*
         * Read individual reports for aggregation
         */
        individualReports = readZeroCodeReportsByPath(TARGET_REPORT_DIR);

        /*
         * Generate: CSV report
         */
        final List<ZeroCodeCsvReport> zeroCodeCsvReportRows = buildCsvRows();
        generateCsvReport(zeroCodeCsvReportRows);

        /*
         * Generate: Chart using HighChart
         */
        HighChartColumnHtml highChartColumnHtml = convertCsvRowsToHighChartData(zeroCodeCsvReportRows);
        generateHighChartReport(highChartColumnHtml);

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
        }
    }

    public List<ZeroCodeCsvReport> buildCsvRows() {
        /*
         * Map the java list to CsvPojo
         */
        ZeroCodeCsvReportBuilder csvFileBuilder = ZeroCodeCsvReportBuilder.newInstance();

        individualReports.forEach(thisReport ->
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

        List<String> allEndPointFiles = getAllEndPointFilesFrom(reportsFolder);

        List<ZeroCodeReport> scenarioReports = allEndPointFiles.stream()
                .map(reportJsonFile -> {
                    try {

                        return mapper.readValue(new File(reportJsonFile), ZeroCodeReport.class);

                    } catch (IOException e) {

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
}