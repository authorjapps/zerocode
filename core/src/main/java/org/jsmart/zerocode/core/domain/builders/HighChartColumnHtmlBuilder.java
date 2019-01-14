package org.jsmart.zerocode.core.domain.builders;

import org.jsmart.zerocode.core.domain.reports.chart.HighChartColumnHtml;

public class HighChartColumnHtmlBuilder {
    String pageTitle;
    String testResult;
    String chartTitleTop;
    String textYaxis;
    String chartSeriesName;
    String chartTitleTopInABox;

    public static HighChartColumnHtmlBuilder newInstance() {
        return new HighChartColumnHtmlBuilder();
    }

    public HighChartColumnHtml build() {
        HighChartColumnHtml built = new HighChartColumnHtml(pageTitle, testResult, chartTitleTop,
                textYaxis, chartSeriesName, chartTitleTopInABox);

        return built;
    }

    public HighChartColumnHtmlBuilder pageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
        return this;
    }

    public HighChartColumnHtmlBuilder testResult(String testResult) {
        this.testResult = testResult;
        return this;
    }

    public HighChartColumnHtmlBuilder chartTitleTop(String chartTitleTop) {
        this.chartTitleTop = chartTitleTop;
        return this;
    }

    public HighChartColumnHtmlBuilder textYaxis(String textYaxis) {
        this.textYaxis = textYaxis;
        return this;
    }

    public HighChartColumnHtmlBuilder chartSeriesName(String chartSeriesName) {
        this.chartSeriesName = chartSeriesName;
        return this;
    }

    public HighChartColumnHtmlBuilder chartTitleTopInABox(String chartTitleTopInABox) {
        this.chartTitleTopInABox = chartTitleTopInABox;
        return this;
    }
}
