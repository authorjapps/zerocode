package org.jsmart.smarttester.core.verify;

import org.jsmart.smarttester.core.runner.ZeroCodeTomcatSuiteRunner;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

//@RunWith(Suite.class) <-- If not via custom runner
@Suite.SuiteClasses({
        ZeroCodeUnitTomcatNavigatorVerification.class,
        ZeroCodeUnitTomcatNavigatorVerificationOther.class
})
@RunWith(ZeroCodeTomcatSuiteRunner.class)
public class ZeroCodeJUnitTomcatSuite {

}