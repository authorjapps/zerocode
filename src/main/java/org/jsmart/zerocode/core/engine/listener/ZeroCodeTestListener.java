package org.jsmart.zerocode.core.engine.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import org.jsmart.zerocode.core.report.ZeroCodeReportGenerator;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Siddha on 24-jul-2016
 */
public class ZeroCodeTestListener extends RunListener {
    private static final org.slf4j.Logger LOGGER = getLogger(ZeroCodeTestListener.class);

    private final ObjectMapper mapper;

    private final ZeroCodeReportGenerator reportGenerator;

    @Inject
    public ZeroCodeTestListener(ObjectMapper mapper, ZeroCodeReportGenerator injectedReportGenerator) {
        this.mapper = mapper;
        this.reportGenerator = injectedReportGenerator;
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

        reportGenerator.generateCsvReport();

        reportGenerator.generateHighChartReport();

    }
}