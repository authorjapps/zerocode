package org.jsmart.zerocode.testhelp.tests;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.JsonTestCases;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.domain.UseHttpClient;
import org.jsmart.zerocode.core.runner.ZeroCodePackageRunner;
import org.jsmart.zerocode.zerocodejavaexec.httpclient.CustomHttpClient;
import org.junit.runner.RunWith;

@TargetEnv("github_host.properties")
@UseHttpClient(CustomHttpClient.class)
@RunWith(ZeroCodePackageRunner.class)
@JsonTestCases({
        @JsonTestCase("no_server/no_server_call_simple.json"),
        @JsonTestCase("no_server/no_server_call_multi.json"),
})
public class HelloWorldCherryPickSuite {
}
