package org.jsmart.smarttester.core.tests;

import org.jsmart.smarttester.core.domain.TargetEnv;
import org.jsmart.smarttester.core.domain.TestPackageRoot;
import org.jsmart.smarttester.core.tests.customrunner.TestOnlySmartRunner;
import org.junit.runner.RunWith;

@TargetEnv("config_hosts.properties")
@TestPackageRoot("07_some_test_cases")
@RunWith(TestOnlySmartRunner.class)
public class SmartPackagedTestCases {

}
