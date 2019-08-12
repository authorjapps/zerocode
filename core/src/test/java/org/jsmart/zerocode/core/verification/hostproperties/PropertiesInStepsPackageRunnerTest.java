package org.jsmart.zerocode.core.verification.hostproperties;

import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.domain.TestPackageRoot;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodePackageRunner;
import org.junit.runner.RunWith;

@TargetEnv("dev_test.properties")
@TestPackageRoot("integration_test_files/host_keys")
@RunWith(TestOnlyZeroCodePackageRunner.class)
public class PropertiesInStepsPackageRunnerTest {

}

