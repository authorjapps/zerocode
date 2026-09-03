package org.jsmart.zerocode.core.s3;

import org.jsmart.zerocode.core.engine.preprocessor.ScenarioExecutionState;

public interface S3Client {
    String execute(String bucketName, String operation, String requestJson, ScenarioExecutionState scenarioExecutionState);
}
