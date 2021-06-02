package org.jsmart.zerocode.zerocodejavaexec.httpclient;

import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.domain.TestPackageRoot;
import org.jsmart.zerocode.core.runner.ZeroCodePackageRunner;
import org.junit.runner.RunWith;

@TargetEnv("hello_github_host.properties")
@TestPackageRoot("/helloworld")
@RunWith(ZeroCodePackageRunner.class)
public class JustHelloWorldSuite {

}
