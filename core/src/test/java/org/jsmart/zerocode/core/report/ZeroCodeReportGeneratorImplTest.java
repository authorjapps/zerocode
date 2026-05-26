package org.jsmart.zerocode.core.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.domain.reports.ZeroCodeReportStep;
import org.jsmart.zerocode.core.domain.reports.csv.ZeroCodeCsvReport;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.jsmart.zerocode.core.constants.ZeroCodeReportConstants.RESULT_FAIL;
import static org.jsmart.zerocode.core.constants.ZeroCodeReportConstants.RESULT_PASS;
import static org.jsmart.zerocode.core.constants.ZeroCodeReportConstants.TARGET_FILE_NAME;
import static org.jsmart.zerocode.core.constants.ZeroCodeReportConstants.TARGET_FULL_REPORT_CSV_FILE_NAME;
import static org.junit.Assert.assertEquals;

public class ZeroCodeReportGeneratorImplTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    private ZeroCodeReportGeneratorImpl zeroCodeReportGenerator;

    ObjectMapper mapper = new ObjectMapperProvider().get();

    @Before
    public void setItUp() throws Exception {

        zeroCodeReportGenerator = new ZeroCodeReportGeneratorImpl(mapper);

    }

    @Test
    public void testReportFolderNotPresentInTarget_validation() throws Exception {
        final String reportsFolder = "/target/helloooo";

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Somehow the '/target/helloooo' is not present or has no report JSON files");
        expectedException.expectMessage("1) No tests were activated or made to run via ZeroCode runner.");
        zeroCodeReportGenerator.readZeroCodeReportsByPath(reportsFolder);

    }

    @Ignore("mvn clean install - removes target folder. So this passes when run without 'clean'" +
            "To fix it create a temp folder, assign to reportsFolder variable and run")
    @Test
    public void testReportFolderPresentInTargetNormalFlow() throws Exception {
        final String reportsFolder = "target/zerocode-test-reports";

        zeroCodeReportGenerator.validateReportsFolderAndTheFilesExists(reportsFolder);

    }


    @Test
    public void testAuthorJiraStyle_LEGACYMARKER() throws Exception {
        List<String> authors;

        // OLD - Deprecated
        authors = zeroCodeReportGenerator.optionalAuthors("PayPal One touch payment @@Peter");
        assertThat(authors.get(0), is("@@Peter"));

        authors = zeroCodeReportGenerator.optionalAuthors("PayPal One touch payment");
        assertThat(authors.size(), is(0));

        authors = zeroCodeReportGenerator.optionalAuthors("PayPal One touch @@payment @@Peter");
        assertThat(authors.get(0), is("@@payment"));
        assertThat(authors.get(1), is("@@Peter"));
        assertThat(authors.size(), is(2));

        authors = zeroCodeReportGenerator.optionalAuthors("PayPal One touch payment @@Peter-Gibson");
        assertThat(authors.get(0), is("@@Peter-Gibson"));

        authors = zeroCodeReportGenerator.optionalAuthors("PayPal One touch payment @@Peter Gibson");
        assertThat(authors.get(0), is("@@Peter"));

        authors = zeroCodeReportGenerator.optionalAuthors("@@Peter- PayPal One touch payment ");
        assertThat(authors.get(0), is("@@Peter-"));
    }

    @Test
    public void testAuthorJiraStyle_new() throws Exception {
        List<String> authors;

        // OLD - Deprecated
        authors = zeroCodeReportGenerator.optionalAuthors("PayPal One touch payment @Peter");
        assertThat(authors.get(0), is("@Peter"));

        authors = zeroCodeReportGenerator.optionalAuthors("PayPal One touch payment");
        assertThat(authors.size(), is(0));

        authors = zeroCodeReportGenerator.optionalAuthors("PayPal One touch @payment @Peter");
        assertThat(authors.get(0), is("@payment"));
        assertThat(authors.get(1), is("@Peter"));
        assertThat(authors.size(), is(2));

        authors = zeroCodeReportGenerator.optionalAuthors("PayPal One touch payment @Peter-Gibson");
        assertThat(authors.get(0), is("@Peter-Gibson"));

        authors = zeroCodeReportGenerator.optionalAuthors("PayPal One touch payment @Peter Gibson");
        assertThat(authors.get(0), is("@Peter"));

        authors = zeroCodeReportGenerator.optionalAuthors("@Peter- PayPal One touch payment ");
        assertThat(authors.get(0), is("@Peter-"));

    }

    @Test
    public void testCategoryHashTag() throws Exception {
        List<String> categories;

        categories = zeroCodeReportGenerator.optionalCategories("PayPal One touch payment #Smoke");
        assertThat(categories.get(0), is("#Smoke"));
        assertThat(categories.size(), is(1));

        categories = zeroCodeReportGenerator.optionalCategories("PayPal One touch #Smoke #PDC");
        assertThat(categories.get(0), is("#Smoke"));
        assertThat(categories.get(1), is("#PDC"));
        assertThat(categories.size(), is(2));

        categories = zeroCodeReportGenerator.optionalCategories("PayPal One touch payment #SIT_Smoke");
        assertThat(categories.get(0), is("#SIT_Smoke"));

        categories = zeroCodeReportGenerator.optionalCategories("PayPal One touch payment #PDC-Gibson");
        assertThat(categories.get(0), is("#PDC-Gibson"));

        categories = zeroCodeReportGenerator.optionalCategories("PayPal One touch payment #PDC");
        assertThat(categories.get(0), is("#PDC"));

        categories = zeroCodeReportGenerator.optionalCategories("#PDC, PayPal One touch payment ");
        assertThat(categories.get(0), is("#PDC,"));

        categories = zeroCodeReportGenerator.optionalCategories("PayPal One touch payment");
        assertThat(categories.size(), is(0));

    }

    @Test
    public void testGettingUniqueStepsForMultipleRetries(){
        List<ZeroCodeReportStep> steps = new ArrayList<ZeroCodeReportStep>(){
            {
                add(new ZeroCodeReportStep("testCorrelationId",RESULT_PASS));
                add(new ZeroCodeReportStep("testCorrelationId",RESULT_FAIL));
            }
        };
        List<ZeroCodeReportStep> uniqueSteps = zeroCodeReportGenerator.getUniqueSteps(steps);
        assertEquals(uniqueSteps.size() , 1);
        assertEquals(uniqueSteps.get(0).getResult(),RESULT_PASS);
    }

    @Test
    public void testGettingUniqueStepsForNoRetries(){
        List<ZeroCodeReportStep> steps = new ArrayList<ZeroCodeReportStep>(){
            {
                add(new ZeroCodeReportStep("testCorrelationId1",RESULT_PASS));
                add(new ZeroCodeReportStep("testCorrelationId2",RESULT_FAIL));
                add(new ZeroCodeReportStep("testCorrelationId3",RESULT_FAIL));
            }
        };
        List<ZeroCodeReportStep> uniqueSteps = zeroCodeReportGenerator.getUniqueSteps(steps);
        assertEquals(uniqueSteps.size() , 3);
        assertEquals(uniqueSteps.stream().filter(step->step.getResult().equals(RESULT_PASS)).count(),1);
        assertEquals(uniqueSteps.stream().filter(step->step.getResult().equals(RESULT_FAIL)).count(),2);

        assertThat(uniqueSteps.get(0).getCorrelationId(),is("testCorrelationId1"));
        assertThat(uniqueSteps.get(1).getCorrelationId(),is("testCorrelationId2"));
        assertThat(uniqueSteps.get(2).getCorrelationId(),is("testCorrelationId3"));

    }

    @Test
    public void resolveHtmlReportName_returnsCustomName_whenKeyPresentInZerocodeProperties() {
        assertThat(zeroCodeReportGenerator.resolveHtmlReportName(), is("target/my-custom-report.html"));
    }

    @Test
    public void resolveCsvReportName_returnsCustomName_whenKeyPresentInZerocodeProperties() {
        assertThat(zeroCodeReportGenerator.resolveCsvReportName(), is("my-custom-granular.csv"));
    }

    @Test
    public void resolveHtmlReportName_returnsDefault_whenZerocodePropertiesIsEmpty() {
        zeroCodeReportGenerator.zerocodeProperties = new Properties(); //setting to empty
        assertThat(zeroCodeReportGenerator.resolveHtmlReportName(), is(TARGET_FILE_NAME));
    }

    @Test
    public void resolveCsvReportName_returnsDefault_whenZerocodePropertiesIsEmpty() {
        zeroCodeReportGenerator.zerocodeProperties = new Properties();
        assertThat(zeroCodeReportGenerator.resolveCsvReportName(), is(TARGET_FULL_REPORT_CSV_FILE_NAME));
    }

    // -------------------------------------------------------------------------
    // buildTableReportContent() unit tests
    // -------------------------------------------------------------------------

    private static ZeroCodeCsvReport csvRow(String scenario, String step, String method,
                                            String result, double delay) {
        return new ZeroCodeCsvReport(scenario, 0, step, 0, "corr-id",
                result, method, "2026-01-01T00:00:00", "2026-01-01T00:00:01", delay);
    }

    @Test
    public void buildTableReport_allRowsHaveSameLineLength() {
        List<ZeroCodeCsvReport> rows = Arrays.asList(
                csvRow("Scenario One", "step_one", "GET", RESULT_PASS, 100.0),
                csvRow("Scenario Two", "step_two", "POST", RESULT_FAIL, 200.0)
        );

        String table = zeroCodeReportGenerator.buildTableReportContent(rows);
        String[] lines = table.split("\n");

        // Every line must be the same display-string length
        // (emoji rows are 1 string-char shorter but emoji is 2-display-wide — check string length)
        int separatorLen = lines[0].length();
        for (String line : lines) {
            assertThat("Line not same width as separator: [" + line + "]",
                    line.length() == separatorLen || line.length() == separatorLen - 1, is(true));
        }
    }

    @Test
    public void buildTableReport_truncatesLongScenarioAt48Chars() {
        String longScenario = "GIVEN the very long scenario name that exceeds the column width limit set for the table";
        List<ZeroCodeCsvReport> rows = Arrays.asList(
                csvRow(longScenario, "step", "GET", RESULT_PASS, 50.0)
        );

        String table = zeroCodeReportGenerator.buildTableReportContent(rows);
        // truncated text ends with ".." and is exactly 48 chars (46 base + "..")
        assertThat(table, containsString("GIVEN the very long scenario name that exceeds.."));
    }

    @Test
    public void buildTableReport_passedRowContainsCheckEmoji() {
        List<ZeroCodeCsvReport> rows = Arrays.asList(
                csvRow("My Scenario", "my_step", "GET", RESULT_PASS, 75.0)
        );
        assertThat(zeroCodeReportGenerator.buildTableReportContent(rows), containsString("PASSED ✅"));
    }

    @Test
    public void buildTableReport_failedRowContainsCrossEmoji() {
        List<ZeroCodeCsvReport> rows = Arrays.asList(
                csvRow("My Scenario", "my_step", "POST", RESULT_FAIL, 0.0)
        );
        assertThat(zeroCodeReportGenerator.buildTableReportContent(rows), containsString("FAILED ❌"));
    }

    @Test
    public void buildTableReport_footerContainsCorrectCounts() {
        List<ZeroCodeCsvReport> rows = Arrays.asList(
                csvRow("S1", "step1", "GET",  RESULT_PASS, 10.0),
                csvRow("S2", "step2", "POST", RESULT_PASS, 20.0),
                csvRow("S3", "step3", "PUT",  RESULT_FAIL, 5.0)
        );

        String table = zeroCodeReportGenerator.buildTableReportContent(rows);
        assertThat(table, containsString("Total: 3"));
        assertThat(table, containsString("PASSED: 2"));
        assertThat(table, containsString("FAILED: 1"));
    }

    @Test
    public void buildTableReport_footerContainsMinMaxDelayWithPipeSeparator() {
        List<ZeroCodeCsvReport> rows = Arrays.asList(
                csvRow("S1", "step1", "GET",  RESULT_PASS, 410.0),
                csvRow("S2", "step2", "POST", RESULT_PASS, 1.0),
                csvRow("S3", "step3", "DELETE", RESULT_FAIL, 0.0)
        );

        String table = zeroCodeReportGenerator.buildTableReportContent(rows);
        assertThat(table, containsString("Min delay: 0.0 ms  |  Max delay: 410.0 ms"));
    }

    @Test
    public void buildTableReport_delayValuesAreRightAligned() {
        List<ZeroCodeCsvReport> rows = Arrays.asList(
                csvRow("S1", "step1", "GET", RESULT_PASS, 1000.0),
                csvRow("S2", "step2", "GET", RESULT_PASS, 1.0)
        );

        String table = zeroCodeReportGenerator.buildTableReportContent(rows);
        // Both delay values right-padded to same field width: "    1000.0" and "       1.0"
        assertThat(table, containsString("    1000.0 |"));
        assertThat(table, containsString("       1.0 |"));
    }

}