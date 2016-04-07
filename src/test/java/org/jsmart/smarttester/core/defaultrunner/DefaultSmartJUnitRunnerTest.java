package org.jsmart.smarttester.core.defaultrunner;

import org.jsmart.smarttester.core.runner.SmartTestCase;
import org.jsmart.smarttester.core.runner.SmartJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(SmartJUnitRunner.class)
public class DefaultSmartJUnitRunnerTest {

    @SmartTestCase("test_default_cases/01_test_json_flow_single_step.json")
    @Test
    public void testASmartTestCase() throws Exception {

    }

    @SmartTestCase("test_default_cases/01_test_json_flow_single_step.json")
    @Test
    public void testASmartTestCase_Another() throws Exception {

    }
}
