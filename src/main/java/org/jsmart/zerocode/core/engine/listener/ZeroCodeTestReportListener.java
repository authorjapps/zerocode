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
public class ZeroCodeTestReportListener extends RunListener {
    private static final org.slf4j.Logger LOGGER = getLogger(ZeroCodeTestReportListener.class);

    private final ObjectMapper mapper;

    private final ZeroCodeReportGenerator reportGenerator;

    @Inject
    public ZeroCodeTestReportListener(ObjectMapper mapper, ZeroCodeReportGenerator injectedReportGenerator) {
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
        LOGGER.info("### ZeroCode: Test run completed for this runner. Generating test reports and charts. " +
                "More help and examples are available at the README in GitHub");
        generateChartsAndReports();
    }

    private void generateChartsAndReports() {

        reportGenerator.generateCsvReport();

        /**
         * Not compatible with open source license i.e. why not activated But if it has to be used inside intranet,
         * then a single Developer's license should do. But visit www.highcharts.com for details.

         * https://shop.highsoft.com/faq
         * If I am using the Software on a commercial company´s intranet, does it require a license?
           Yes. The Developer License allows you to install and use the software on a commercial company's intranet.
         */
        //reportGenerator.generateHighChartReport();

        reportGenerator.generateExtentReport();
    }
}