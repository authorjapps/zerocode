package org.jsmart.zerocode.core.runner;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.notification.RunNotifier;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ZeroCodeUnitRunnerTest {

    ZeroCodeUnitRunner zeroCodeUnitRunner;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    public static class TinySmartJUnitRunnerExampleTester {

        @JsonTestCase("/abcd/path")
        @Test
        public void tinyTestCase2() throws Exception {
        }

        @JsonTestCase("/mac-daddy") //<---- This one will be first in the list, alphabetically sorted
        @Test
        public void tinyTestCase1() throws Exception {
        }
    }

    @Before
    public void initializeRunner() throws Exception {
        zeroCodeUnitRunner = new ZeroCodeUnitRunner(TinySmartJUnitRunnerExampleTester.class);
    }

    @Test
    public void testWillReadTheAnnotationAndRunVia_BlockJunitRunner() throws Exception {
        assertThat(zeroCodeUnitRunner.getSmartTestCaseNames().size(), is(2));
        assertThat(zeroCodeUnitRunner.getSmartTestCaseNames().get(0), is("/mac-daddy"));
    }

    @Test
    public void testWillReadTheAnnotationAnd_Notify() throws Exception {
        zeroCodeUnitRunner.run(new RunNotifier());
        assertThat(zeroCodeUnitRunner.getCurrentTestCase(), is("/abcd/path"));
    }
}