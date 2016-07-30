package org.jsmart.zerocode.core.verify;

import org.jsmart.zerocode.core.domain.annotation.TargetEnv;
import org.jsmart.zerocode.core.domain.annotation.TestPackageRoot;
import org.jsmart.zerocode.core.runner.ZeroCodePackageRunner;
import org.junit.runner.RunWith;

@TargetEnv("dev_test.properties")
@TestPackageRoot("01_verification_test_cases")
@RunWith(ZeroCodePackageRunner.class)
public class SmartPackagedVerification {

}
