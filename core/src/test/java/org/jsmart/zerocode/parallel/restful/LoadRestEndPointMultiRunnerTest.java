package org.jsmart.zerocode.parallel.restful;

import org.jsmart.zerocode.core.domain.LoadWith;
import org.jsmart.zerocode.core.domain.TestMapping;
import org.jsmart.zerocode.core.runner.parallel.ZeroCodeMultiLoadRunner;
import org.junit.runner.RunWith;


@LoadWith("load_config_test.properties")
@TestMapping(testClass = JunitRestTestSample.class, testMethod = "testGetCallToHome_pass")
@TestMapping(testClass = JunitRestTestSample.class, testMethod = "testGetCallToHome_pass")
//@TestMapping(testClass = JunitRestTestSample.class, testMethod = "testGetCallToHome_fail")
@RunWith(ZeroCodeMultiLoadRunner.class)
public class LoadRestEndPointMultiRunnerTest {

}