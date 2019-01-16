package org.jsmart.zerocode.core.domain.reports.chart;

public class HighChartColumnHtml {
    String pageTitle;
    String testResult;
    String chartTitleTop;
    String textYaxis;
    String chartSeriesName;
    String chartTitleTopInABox;

    public HighChartColumnHtml(String pageTitle, String testResult, String chartTitleTop, String textYaxis, String chartSeriesName, String chartTitleTopInABox) {
        this.pageTitle = pageTitle;
        this.testResult = testResult;
        this.chartTitleTop = chartTitleTop;
        this.textYaxis = textYaxis;
        this.chartSeriesName = chartSeriesName;
        this.chartTitleTopInABox = chartTitleTopInABox;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public String getTestResult() {
        return testResult;
    }

    public String getChartTitleTop() {
        return chartTitleTop;
    }

    public String getTextYaxis() {
        return textYaxis;
    }

    public String getChartSeriesName() {
        return chartSeriesName;
    }

    public String getChartTitleTopInABox() {
        return chartTitleTopInABox;
    }

}
