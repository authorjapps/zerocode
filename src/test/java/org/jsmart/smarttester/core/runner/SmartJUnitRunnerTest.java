package org.jsmart.smarttester.core.runner;

import org.jsmart.smarttester.core.domain.SmartTestCase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.notification.RunNotifier;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SmartJUnitRunnerTest {

    SmartJUnitRunner smartJUnitRunner;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    public static class TinySmartJUnitRunnerExampleTester {

        @SmartTestCase("/abcd/path")
        @Test
        public void tinyTestCase2() throws Exception {
        }

        @SmartTestCase("/mac-daddy") //<---- This one will be first in the list, alphabetically sorted
        @Test
        public void tinyTestCase1() throws Exception {
        }
    }

    @Before
    public void initializeRunner() throws Exception {
        smartJUnitRunner = new SmartJUnitRunner(TinySmartJUnitRunnerExampleTester.class);
    }

    @Test
    public void testWillReadTheAnnotationAndRunVia_BlockJunitRunner() throws Exception {
        assertThat(smartJUnitRunner.getSmartTestCaseNames().size(), is(2));
        assertThat(smartJUnitRunner.getSmartTestCaseNames().get(0), is("/mac-daddy"));
    }

    @Test
    public void testWillReadTheAnnotationAnd_Notify() throws Exception {
        smartJUnitRunner.run(new RunNotifier());
        assertThat(smartJUnitRunner.getCurrentTestCase(), is("/abcd/path"));
    }
}