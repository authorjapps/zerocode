package org.jsmart.smarttester.core.runner;

import com.google.inject.Inject;
import org.jsmart.smarttester.core.di.SmartServiceModule;
import org.jsmart.smarttester.core.domain.FlowSpec;
import org.jsmart.smarttester.core.utils.SmartUtils;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JukitoRunner.class)
@UseModules(SmartServiceModule.class)
public class SmartRunnerTest {

    @Inject
    SmartUtils smartUtils;

    SmartRunner smartRunner;

    @TestPackageRoot("test_one_multi_steps")
    public static class FlowSpecExampleTest {
    }

    @Before
    public void initializeRunner() throws Exception {
        smartRunner = new SmartRunner(FlowSpecExampleTest.class, smartUtils);
    }

    @Test
    public void willHaveListOf_TestCases_Here() throws Exception {
        List<FlowSpec> children = smartRunner.getChildren();
        assertThat(children.size(), is(2));
    }

    @Test
    public void willHaveListOf_TestCases_Frompackage() throws Exception {
        smartRunner = new SmartRunner(FlowExamplePackagePickerClass.class, smartUtils);
        List<FlowSpec> children = smartRunner.getChildren();
        assertThat(children.size(), is(2));
    }
}