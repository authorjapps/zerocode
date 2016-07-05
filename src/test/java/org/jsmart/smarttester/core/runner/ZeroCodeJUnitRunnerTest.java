package org.jsmart.smarttester.core.runner;

import org.jsmart.smarttester.core.domain.SmartTestCase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.notification.RunNotifier;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ZeroCodeJUnitRunnerTest {

    ZeroCodeJUnitRunner zeroCodeJUnitRunner;

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
        zeroCodeJUnitRunner = new ZeroCodeJUnitRunner(TinySmartJUnitRunnerExampleTester.class);
    }

    @Test
    public void testWillReadTheAnnotationAndRunVia_BlockJunitRunner() throws Exception {
        assertThat(zeroCodeJUnitRunner.getSmartTestCaseNames().size(), is(2));
        assertThat(zeroCodeJUnitRunner.getSmartTestCaseNames().get(0), is("/mac-daddy"));
    }

    @Test
    public void testWillReadTheAnnotationAnd_Notify() throws Exception {
        zeroCodeJUnitRunner.run(new RunNotifier());
        assertThat(zeroCodeJUnitRunner.getCurrentTestCase(), is("/abcd/path"));
    }
}