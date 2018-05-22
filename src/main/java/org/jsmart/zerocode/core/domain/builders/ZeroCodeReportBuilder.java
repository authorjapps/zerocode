package org.jsmart.zerocode.core.domain.builders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsmart.zerocode.core.di.ObjectMapperProvider;
import org.jsmart.zerocode.core.domain.reports.ZeroCodeExecResult;
import org.jsmart.zerocode.core.domain.reports.ZeroCodeReport;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.TARGET_REPORT_DIR;
import static org.slf4j.LoggerFactory.getLogger;

public class ZeroCodeReportBuilder {
    private static final org.slf4j.Logger LOGGER = getLogger(ZeroCodeReportBuilder.class);
    public static final int REPORT_WRITING_THREAD_POOL = 5;

    private LocalDateTime timeStamp;
    private List<ZeroCodeExecResult> results = Collections.synchronizedList(new ArrayList());
    private ZeroCodeReport built;

    private ExecutorService executorService = Executors.newFixedThreadPool(REPORT_WRITING_THREAD_POOL);

    public static ZeroCodeReportBuilder newInstance() {
        return new ZeroCodeReportBuilder();
    }

    public ZeroCodeReport build() {
        ZeroCodeReport built = new ZeroCodeReport(timeStamp, results);
        this.built = built;

        return built;
    }

    public ZeroCodeReportBuilder timeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public ZeroCodeReportBuilder results(List<ZeroCodeExecResult> results) {
        this.results = results;
        return this;
    }

    public ZeroCodeReportBuilder result(ZeroCodeExecResult result) {
        this.results.add(result);
        return this;
    }

    public synchronized void printToFile(String fileName) {
        try {
            this.build();

            final ObjectMapper mapper = new ObjectMapperProvider().get();

            File file = new File(TARGET_REPORT_DIR + fileName);
            file.getParentFile().mkdirs();
            mapper.writeValue(file, this.built);
            delay(100L);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            LOGGER.warn("### Report Generation Problem: There was a problem during JSON parsing. Details: " + e);

        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.warn("### Report Generation Problem: There was a problem during writing the report. Details: " + e);
        }
    }

    private void delay(long miliSec) {
        try {
            Thread.sleep(miliSec);
        } catch (InterruptedException e) {
            throw new RuntimeException("Error providing delay");
        }
    }


    public void printToFileAsync(String fileName) {
        this.build();
        final ObjectMapper mapper = new ObjectMapperProvider().get();

        LOGGER.info("executorService(hashCode)>>" + executorService.hashCode());

        executorService.execute(() -> {
            LOGGER.info("Writing to file async - " + fileName);
            File file = new File(TARGET_REPORT_DIR + fileName);
            file.getParentFile().mkdirs();
            try {
                mapper.writeValue(file, built);
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.warn("### Report Generation Problem: There was a problem during writing the report. Details: " + e);
            }
        });

        shutDownExecutorGraceFully();
    }

    private void shutDownExecutorGraceFully() {
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            // wait for all tasks to finish executing
            // LOGGER.info("Still waiting for all threads to complete execution...");
        }
        LOGGER.info("Pass-Fail JSON report written target -done. Finished all threads");
    }
}
