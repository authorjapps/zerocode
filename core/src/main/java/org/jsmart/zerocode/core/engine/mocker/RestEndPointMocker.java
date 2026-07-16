package org.jsmart.zerocode.core.engine.mocker;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestEndPointMocker {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestEndPointMocker.class);

    public static int createWithLocalMock(String endPointJsonApi) {
        if (StringUtils.isNotEmpty(endPointJsonApi)) {
            // read this json into simulator.
        }

        return 200;
    }

    /*
     * This is working code, whenever you put the virtuoso dependency here, you can uncomment this block.
     */
    public static int createWithVirtuosoMock(String endPointJsonApi) {
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

}
