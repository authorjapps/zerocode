package org.jsmart.zerocode.core.engine.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.inject.Inject;
import org.jsmart.zerocode.core.domain.reports.ZeroCodeCsvReport;
import org.jsmart.zerocode.core.domain.reports.ZeroCodeReport;
import org.jsmart.zerocode.core.domain.reports.builders.ZeroCodeCsvReportBuilder;
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
    private static final org.slf4j.Logger LOGGER = getLogger(ZeroCodeMultiStepsScenarioRunnerImpl.class);

    private final ObjectMapper mapper;

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
        LOGGER.info("### ZeroCode: all testRunFinished");

        generateCsvReport();


    }

    public void generateCsvReport() {
        /*
         * Read target files all into a report
         */
        final List<ZeroCodeReport> allReports = readZeroCodeReportsByPath(TARGET_REPORT_DIR);

        /*
         * Map the java list to CsvPojo
         */
        List<ZeroCodeCsvReport> csvRows = new ArrayList<>();
        ZeroCodeCsvReportBuilder csvFileBuilder = ZeroCodeCsvReportBuilder.newInstance();

        allReports.forEach(thisReport ->
            thisReport.getResults().forEach(thisResult -> {

                csvFileBuilder.scenarioLoop(thisResult.getLoop());
                csvFileBuilder.scenarioName(thisResult.getScenarioName());

                thisResult.getSteps().forEach(thisStep -> {
                    csvFileBuilder.stepLoop(thisStep.getLoop());
                    csvFileBuilder.stepName(thisStep.getName());
                    csvFileBuilder.correlationId(thisStep.getCorrelationId()); //<-- to search in the log
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
                    ".csv"), csvRows);
        } catch (IOException e) {
            e.printStackTrace();
        }
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