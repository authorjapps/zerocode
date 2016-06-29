package org.jsmart.smarttester.core.engine.mocker;

import org.apache.commons.lang.StringUtils;
import org.jsmart.smarttester.core.runner.MultiStepsScenarioRunnerImpl;

import java.util.logging.Logger;

public class RestEndPointMocker {
    private static final Logger logger = Logger.getLogger(MultiStepsScenarioRunnerImpl.class.getName());

    /*
     * This is working code, whenever you put the virtuoso dependency here, you can uncomment this block.
     */
    public static int createVirtuosoMock(String endPointJsonApi) {
//        if(StringUtils.isNotEmpty(endPointJsonApi)){
//            ApiSpec apiSpec = SimulatorJsonUtils.deserialize(endPointJsonApi);
//            apiSpec.getApis().stream()
//                    .forEach(api -> {
//                        int status = aVirtuosoRestMocker()
//                                .url(api.getUrl())
//                                .operation(api.getOperation())
//                                .willReturn(
//                                        aResponse()
//                                                .status(api.getResponse().getStatus())
//                                                .body(api.getResponse().getBody())
//                                                .build()
//                                );
//
//                        if(200 != status){
//                            logbuilder.info("Mocking virtuoso end point failed. Status: " + status);
//                            throw new RuntimeException("Mocking virtuoso end point failed. Status: " + status + ". Check tunnel etc.");
//                        }
//                    });
//        }

        return 200;
    }

    public static int createLocalMock(String endPointJsonApi) {
        if(StringUtils.isNotEmpty(endPointJsonApi)){
            // read this json into virtuoso.
        }

        return 200;
    }
}