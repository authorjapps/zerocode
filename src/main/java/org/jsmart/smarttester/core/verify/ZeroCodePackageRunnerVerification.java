package org.jsmart.smarttester.core.verify;

import org.jsmart.smarttester.core.domain.TargetEnv;
import org.jsmart.smarttester.core.domain.TestPackageRoot;
import org.jsmart.smarttester.core.runner.ZeroCodePackageRunner;
import org.junit.runner.RunWith;

@TargetEnv("dev_test.properties")
@TestPackageRoot("01_verification_test_cases")
@RunWith(ZeroCodePackageRunner.class)
public class ZeroCodePackageRunnerVerification {

}
