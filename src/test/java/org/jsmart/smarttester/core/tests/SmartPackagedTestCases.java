package org.jsmart.smarttester.core.tests;

import org.jsmart.smarttester.core.domain.TargetEnv;
import org.jsmart.smarttester.core.domain.TestPackageRoot;
import org.jsmart.smarttester.core.tests.customrunner.TestOnlyZeroCodePackageRunner;
import org.junit.runner.RunWith;

@TargetEnv("config_hosts.properties")
@TestPackageRoot("07_some_test_cases")
@RunWith(TestOnlyZeroCodePackageRunner.class)
public class SmartPackagedTestCases {

}
