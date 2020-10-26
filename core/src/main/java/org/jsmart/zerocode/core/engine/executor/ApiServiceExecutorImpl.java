package org.jsmart.zerocode.core.engine.executor;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.jsmart.zerocode.core.engine.executor.httpapi.HttpApiExecutor;
import org.jsmart.zerocode.core.engine.executor.javaapi.JavaMethodExecutor;
import org.jsmart.zerocode.core.engine.preprocessor.ScenarioExecutionState;
import org.jsmart.zerocode.core.kafka.client.BasicKafkaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiServiceExecutorImpl implements ApiServiceExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiServiceExecutorImpl.class);

    @Inject
    private JavaMethodExecutor javaMethodExecutor;

    @Inject
    private HttpApiExecutor httpApiExecutor;

    @Inject
    private BasicKafkaClient kafkaClient;

    @Inject(optional = true)
    @Named("mock.api.port")
    private int mockPort;

    public ApiServiceExecutorImpl() {
    }

    @Override
    public String executeHttpApi(String url, String methodName, String requestJson) {
        try {
            return httpApiExecutor.execute(url, methodName, requestJson);
        } catch (Throwable severError) {
            LOGGER.error("Ooooooooooops! Something unexpected happened while connecting to the url:{} " +
                    "\n1) Check if the service is running at the host -or-" +
                    "\n2) Check the corporate proxy has been configured correctly -or" +
                    "\n3) Choose another mocking(if in use) port not to conflict with the port:{} -or-" +
                    "\n4) Restart the service. -or- " +
                    "See the full error details below-\n{}", url, mockPort, severError);

            throw new RuntimeException(severError);
        }
    }

    @Override
    public String executeJavaOperation(String className, String methodName, String requestJson) {
        try{
            return javaMethodExecutor.execute(className, methodName, requestJson);
        } catch (Exception e) {
            LOGGER.error("Java method execution exception - " + e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String executeKafkaService(String kafkaServers, String kafkaTopic, String operation, String requestJson, ScenarioExecutionState scenarioExecutionState) {
        return kafkaClient.execute(kafkaServers, kafkaTopic, operation, requestJson, scenarioExecutionState);
    }
}
