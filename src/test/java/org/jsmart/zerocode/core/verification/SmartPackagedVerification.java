package org.jsmart.zerocode.core.verification;

import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.domain.TestPackageRoot;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodePackageRunner;
import org.junit.runner.RunWith;

@TargetEnv("dev_test.properties")
@TestPackageRoot("01_verification_test_cases")
//@RunWith(ZeroCodePackageRunner.class)
@RunWith(TestOnlyZeroCodePackageRunner.class)
public class SmartPackagedVerification {

}
