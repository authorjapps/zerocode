package org.jsmart.zerocode.core.report;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ZeroCodeReportGeneratorImplTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    private ZeroCodeReportGeneratorImpl zeroCodeReportGenerator;


    @Before
    public void setItUp() throws Exception {

        zeroCodeReportGenerator = new ZeroCodeReportGeneratorImpl();

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

}