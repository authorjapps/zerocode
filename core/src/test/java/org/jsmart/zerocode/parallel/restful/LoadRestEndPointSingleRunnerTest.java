package org.jsmart.zerocode.parallel.restful;

import org.jsmart.zerocode.core.domain.LoadWith;
import org.jsmart.zerocode.core.domain.TestMapping;
import org.jsmart.zerocode.core.runner.parallel.ZeroCodeLoadRunner;
import org.junit.runner.RunWith;

@LoadWith("load_config_test.properties")
@TestMapping(testClass = JunitRestTestSample.class, testMethod = "testGetCallToHome_pass")
@RunWith(ZeroCodeLoadRunner.class)
public class LoadRestEndPointSingleRunnerTest {

}