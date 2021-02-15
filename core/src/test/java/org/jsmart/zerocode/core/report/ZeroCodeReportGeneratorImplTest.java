package org.jsmart.zerocode.core.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.domain.reports.ZeroCodeReportStep;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.jsmart.zerocode.core.constants.ZeroCodeReportConstants.RESULT_FAIL;
import static org.jsmart.zerocode.core.constants.ZeroCodeReportConstants.RESULT_PASS;
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
    public void testAuthorJiraStyle() throws Exception {
        String author;

        // OLD - Deprecated
        author = zeroCodeReportGenerator.optionalAuthor("PayPal One touch payment @@Peter@@");
        assertThat(author, is("Peter"));

        author = zeroCodeReportGenerator.optionalAuthor("PayPal One touch @@payment @@Peter@@");
        assertThat(author, is("payment "));

        author = zeroCodeReportGenerator.optionalAuthor("PayPal One touch payment @@Peter Gibson@@");
        assertThat(author, is("Peter Gibson"));

        author = zeroCodeReportGenerator.optionalAuthor("PayPal One touch payment @@Peter Gibson");
        assertThat(author, is("Peter"));

        author = zeroCodeReportGenerator.optionalAuthor("PayPal One touch payment @@Peter");
        assertThat(author, is("Peter"));

        author = zeroCodeReportGenerator.optionalAuthor("@@Peter, PayPal One touch payment ");
        assertThat(author, is("Peter"));

        author = zeroCodeReportGenerator.optionalAuthor("PayPal One touch payment");
        assertThat(author, is("Anonymous"));

    }

    @Test
    public void testAuthorJiraStyle_new() throws Exception {
        String author;

        author = zeroCodeReportGenerator.optionalAuthor("PayPal One touch payment @Peter@");
        assertThat(author, is("Peter"));

        author = zeroCodeReportGenerator.optionalAuthor("PayPal One touch @payment @Peter@");
        assertThat(author, is("payment "));

        author = zeroCodeReportGenerator.optionalAuthor("PayPal One touch payment @Peter Gibson@");
        assertThat(author, is("Peter Gibson"));

        author = zeroCodeReportGenerator.optionalAuthor("PayPal One touch payment @Peter Gibson");
        assertThat(author, is("Peter"));

        author = zeroCodeReportGenerator.optionalAuthor("PayPal One touch payment @Peter");
        assertThat(author, is("Peter"));

        author = zeroCodeReportGenerator.optionalAuthor("@Peter, PayPal One touch payment ");
        assertThat(author, is("Peter"));

        author = zeroCodeReportGenerator.optionalAuthor("PayPal One touch payment");
        assertThat(author, is("Anonymous"));

    }

    @Test
    public void testCategoryHashTag() throws Exception {
        String author;

        author = zeroCodeReportGenerator.optionalCategory("PayPal One touch payment #Smoke#");
        assertThat(author, is("Smoke"));

        author = zeroCodeReportGenerator.optionalCategory("PayPal One touch #Smoke #PDC#");
        assertThat(author, is("Smoke "));

        author = zeroCodeReportGenerator.optionalCategory("PayPal One touch payment #SIT Smoke#");
        assertThat(author, is("SIT Smoke"));

        author = zeroCodeReportGenerator.optionalCategory("PayPal One touch payment #PDC Gibson");
        assertThat(author, is("PDC"));

        author = zeroCodeReportGenerator.optionalCategory("PayPal One touch payment #PDC");
        assertThat(author, is("PDC"));

        author = zeroCodeReportGenerator.optionalCategory("#PDC, PayPal One touch payment ");
        assertThat(author, is("PDC"));

        author = zeroCodeReportGenerator.optionalCategory("PayPal One touch payment");
        assertThat(author, is("Anonymous"));

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

}