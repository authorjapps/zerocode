package org.jsmart.zerocode.testhelp.tests.metadatatest;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("github_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class MetaDataTest {
    @Test
    @JsonTestCase("metadatatest/metadatatest.json")
    public void testMetaData() throws Exception {

    }
}