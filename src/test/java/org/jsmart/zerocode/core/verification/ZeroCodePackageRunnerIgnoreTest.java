package org.jsmart.zerocode.core.verification;

import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.domain.TestPackageRoot;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodePackageRunner;
import org.junit.Ignore;
import org.junit.runner.RunWith;

@Ignore
@TargetEnv("dev_test.properties")
@TestPackageRoot("01_verification_test_cases")
@RunWith(TestOnlyZeroCodePackageRunner.class)
public class ZeroCodePackageRunnerIgnoreTest {

}
