package org.jsmart.zerocode.testhelp.tests.hellowworldschema;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.Schema;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("github_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class JustHelloWorldSchemaTest {

    @Test
    @Scenario("helloworldschema/hello_world_status_ok_assertions.json")
    @Schema("helloworldschema/hello_world_status_ok_assertions_schema.json")
    public void testGet() throws Exception {
    }

}