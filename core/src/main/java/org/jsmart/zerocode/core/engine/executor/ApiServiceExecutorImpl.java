package org.jsmart.zerocode.core.engine.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.util.List;
import org.jsmart.zerocode.core.kafka.client.BasicKafkaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jsmart.zerocode.core.utils.SmartUtils.prettyPrintJson;

public class ApiServiceExecutorImpl implements ApiServiceExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiServiceExecutorImpl.class);

    @Inject
    private JavaMethodExecutor javaMethodExecutor;

    @Inject
    private ObjectMapper objectMapper;

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
    public String executeJavaOperation(String serviceName, String methodName, String requestJson) {

        if (javaMethodExecutor == null) {
            throw new RuntimeException("Can not proceed as the framework could not load the executors. ");
        }

        List<Class<?>> parameterTypes = javaMethodExecutor.getParameterTypes(serviceName, methodName);

        try {
            Object result;

            if (parameterTypes == null || parameterTypes.size() == 0) {

                result = javaMethodExecutor.execute(serviceName, methodName);

            } else {

                Object request = objectMapper.readValue(requestJson, parameterTypes.get(0));
                result = javaMethodExecutor.execute(serviceName, methodName, request);
            }

            final String resultJson = objectMapper.writeValueAsString(result);
            return prettyPrintJson(resultJson);

        } catch (Exception e) {

            throw new RuntimeException(e);

        }
    }

    @Override
    public String executeHttpApi(String urlName, String methodName, String requestJson) {
        try {

            return httpApiExecutor.execute(urlName, methodName, requestJson);

        } catch (Throwable severError) {
            LOGGER.error("Ooooooooooops! Something unexpected happened while connecting to the url:{} " +
                    "\n1) Check if the service is running at the host -or-" +
                    "\n2) Check the corporate proxy has been configured correctly -or" +
                    "\n3) Choose another mocking(if in use) port not to conflict with the port:{} -or-" +
                    "\n4) Restart the service. -or- " +
                    "See the full error details below-\n{}", urlName, mockPort, severError);

            throw new RuntimeException(severError);
        }
    }

    @Override
    public String executeKafkaService(String kafkaServers, String topicName, String operation, String requestJson) {
        return kafkaClient.execute(kafkaServers, topicName, operation, requestJson);
    }
}
