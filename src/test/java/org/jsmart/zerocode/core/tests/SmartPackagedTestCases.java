package org.jsmart.zerocode.core.tests;

import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.domain.TestPackageRoot;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodePackageRunner;
import org.junit.runner.RunWith;

@TargetEnv("config_hosts.properties")
@TestPackageRoot("07_some_test_cases")
@RunWith(TestOnlyZeroCodePackageRunner.class)
public class SmartPackagedTestCases {

}
