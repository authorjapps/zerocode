package org.jsmart.smarttester.core.verification;

import org.jsmart.smarttester.core.domain.TargetEnv;
import org.jsmart.smarttester.core.domain.TestPackageRoot;
import org.jsmart.smarttester.core.runner.SmartRunner;
import org.junit.runner.RunWith;

@TargetEnv("dev_test.properties")
@TestPackageRoot("01_verification_test_cases")
@RunWith(SmartRunner.class)
public class SmartPackagedVerification {

}
