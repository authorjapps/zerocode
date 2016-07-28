package org.jsmart.zerocode.core.report;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.jsmart.zerocode.core.domain.reports.chart.HighChartColumnHtml;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;

import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.HIGH_CHART_HTML_FILE_NAME;
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.TARGET_FULL_REPORT_DIR;
import static org.slf4j.LoggerFactory.getLogger;

public class HighChartColumnHtmlWriter {
    private static final org.slf4j.Logger LOGGER = getLogger(HighChartColumnHtmlWriter.class);

    public static final String VELOCITY_HIGH_CHART_DEFAULT_FILE = "reports/01_high_chart_column.vm";

    private VelocityEngine vEngine = new VelocityEngine();

    private String templateFile;

    public HighChartColumnHtmlWriter() {
    }

    public HighChartColumnHtmlWriter(String templateFile) {
        this.templateFile = templateFile;
    }

    public String generateHighChart(HighChartColumnHtml highChartColumnHtml){
        vEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        vEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

        vEngine.init();

        VelocityContext context = new VelocityContext();

        /*  add the htmlReport Params to a VelocityContext  */
        context.put("highChartColumnHtml", highChartColumnHtml);

        /*  get the Template  */
        Template t = vEngine.getTemplate(getTemplateFileElseDefault());

        /*  now render the template into a Writer  */
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(
                    TARGET_FULL_REPORT_DIR +
                    HIGH_CHART_HTML_FILE_NAME +
                    LocalDateTime.now().toString().replace(":", "-") +
                    ".html"
            );

            t.merge(context, fileWriter);
            fileWriter.close();
        } catch (IOException chartEx) {
            chartEx.printStackTrace();
            LOGGER.error("Problem occurred during generating test chart. Detail: " + chartEx);

            /*
             * Do not throw exception as this exception is not part of a test execution.
             */
             // throw new RuntimeException(chartEx);
        }

        /* Write to a string - Unit test purpose */
        StringWriter writer = new StringWriter();
        t.merge(context, writer);

        /* use the output */
        final String htmlOut = writer.toString();

        return htmlOut;
    }

    private String getTemplateFileElseDefault() {

        if(templateFile != null){
            return templateFile;
        }

        return VELOCITY_HIGH_CHART_DEFAULT_FILE;
    }

}
