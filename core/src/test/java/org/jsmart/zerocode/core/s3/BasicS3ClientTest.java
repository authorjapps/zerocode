package org.jsmart.zerocode.core.s3;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsmart.zerocode.core.di.main.ApplicationMainModule;
import org.jsmart.zerocode.core.engine.preprocessor.ScenarioExecutionState;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class BasicS3ClientTest {

    @Test
    public void testS3ActionParsing() {
        S3Action upload = S3Action.fromString("UPLOAD");
        assertThat(upload, is(S3Action.UPLOAD));
        
        S3Action list = S3Action.fromString("list");
        assertThat(list, is(S3Action.LIST));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testS3ActionParsing_Invalid() {
        S3Action.fromString("INVALID");
    }
}
