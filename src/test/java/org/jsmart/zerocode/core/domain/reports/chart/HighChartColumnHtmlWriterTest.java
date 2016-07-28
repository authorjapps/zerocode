package org.jsmart.zerocode.core.domain.reports.chart;

import org.jsmart.zerocode.core.report.HighChartColumnHtmlWriter;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class HighChartColumnHtmlWriterTest {

    HighChartColumnHtmlWriter highChartColumnHtmlWriter;

    @Test
    public void willGenerateHtmlTextString() throws Exception {

        highChartColumnHtmlWriter =
                new HighChartColumnHtmlWriter("12_report_velocity/01_high_chart_column_test.vm");

        HighChartColumnHtml highChartColumnHtml = new HighChartColumnHtml(
                "ZC Page Title",
                "[['apple', 50], ['windows', 25]]",
                "chart title top",
                "test Y Axis",
                "chart Series Name",
                "Chart title Top in a Box");

        final String htmlOut = highChartColumnHtmlWriter.generateHighChart(highChartColumnHtml);

        /* assert resolved strings */
        assertThat(htmlOut, containsString("[['apple', 50], ['windows', 25]]"));
        assertThat(htmlOut, containsString("Chart title Top in a Box"));
        assertThat(htmlOut, containsString("ZC Page Title"));
        assertThat(htmlOut, containsString("chart Series Name"));
    }
}