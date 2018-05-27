package org.jsmart.zerocode.core.runner.parallel;

import org.jsmart.zerocode.core.domain.LoadWith;
import org.jsmart.zerocode.core.domain.TestMapping;
import org.jsmart.zerocode.core.domain.TestMappings;
import org.jsmart.zerocode.parallel.restful.JunitRestTestSample;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

// TODO- wip
public class ZeroCodeMultiLoadRunnerTest {


    @LoadWith("load_config_test.properties")
    @TestMappings({
            @TestMapping(testClass = JunitRestTestSample.class, testMethod = "testGetCallToHome_pass"),
            @TestMapping(testClass = JunitRestTestSample.class, testMethod = "testGetCallToHome_fail"),
    })
    @RunWith(ZeroCodeMultiLoadRunner.class)
    public class LoadRestEndPointRunnerTest { //note "public" is necessary

    }

    ZeroCodeMultiLoadRunner zeroCodePackageRunner;

    @Before
    public void initializeRunner() throws Exception {
        zeroCodePackageRunner = new ZeroCodeMultiLoadRunner(LoadRestEndPointRunnerTest.class);
    }

    @Test
    public void testPair_size() {
        List<TestMapping> children = zeroCodePackageRunner.getChildren();
        assertThat(children.size(), is(2));
    }

    @Test
    public void testPair_annotaionMulti() {
        TestMapping[] mappings = LoadRestEndPointRunnerTest.class.getAnnotationsByType(TestMapping.class);
        assertThat(mappings[0].testMethod(), is("testGetCallToHome_pass"));
        assertThat(mappings[0].testClass().getName(), is((JunitRestTestSample.class.getName())));
    }

    @Test
    public void testPair_classVsMethodMap() {
        List<TestMapping> children = zeroCodePackageRunner.getChildren();
        TestMapping[] testMappings = LoadRestEndPointRunnerTest.class.getAnnotationsByType(TestMapping.class);
        assertThat(children.get(0).testMethod(), is("testGetCallToHome_pass"));
        assertThat(children.get(0).testClass().getName(), is((JunitRestTestSample.class.getName())));
    }

    @Test
    public void testLoad_properties() {
        LoadWith loadWithFile = LoadRestEndPointRunnerTest.class.getAnnotation(LoadWith.class);
        assertThat(loadWithFile.value(), is("load_config_test.properties"));
    }

}