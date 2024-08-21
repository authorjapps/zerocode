package org.jsmart.zerocode.testhelp.tests.MetaDataTest;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("github_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class MetaDataTest {
    @Test
    @JsonTestCase("MetaDataTest/MetaDataTest.json")
    public void testmetadata() throws Exception {

    }
}