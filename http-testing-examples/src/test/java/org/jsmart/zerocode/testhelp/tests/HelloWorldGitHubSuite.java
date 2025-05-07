package org.jsmart.zerocode.testhelp.tests;

import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.domain.TestPackageRoot;
import org.jsmart.zerocode.core.domain.UseHttpClient;
import org.jsmart.zerocode.core.runner.ZeroCodePackageRunner;
import org.jsmart.zerocode.zerocodejavaexec.httpclient.CustomHttpClient;
import org.junit.runner.RunWith;

@TargetEnv("github_host.properties")
@UseHttpClient(CustomHttpClient.class)
@RunWith(ZeroCodePackageRunner.class)
@TestPackageRoot("helloworld_github_REST_api") //<--- Root of the package to pick all tests including sub-folders
public class HelloWorldGitHubSuite {
}
