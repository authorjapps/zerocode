package org.jsmart.smarttester.core.logbuilder;

import org.slf4j.Logger;

import java.time.Duration;

public class LogCorelationshipPrinter {

    Logger logger;
    RequestLogBuilder requestLogBuilder = new RequestLogBuilder();
    ResponseLogBuilder responseLogBuilder = new ResponseLogBuilder();

    public LogCorelationshipPrinter(Logger logger) {
        this.logger = logger;
    }

    public static LogCorelationshipPrinter newInstance(Logger logger) {
        return new LogCorelationshipPrinter(logger);
    }

    public RequestLogBuilder aRequestBuilder() {
        return requestLogBuilder;
    }

    public ResponseLogBuilder aResponseBuilder() {
        return responseLogBuilder;

    }

    public void print() {
        System.out.println("#### Diff" + requestLogBuilder.getRequestTimeStamp() +
                responseLogBuilder.getResponseTimeStamp());

        logger.info(String.format("%s %s \nResponse delay:%s milli-secs \n-done-\n\n",
                requestLogBuilder.toString(),
                responseLogBuilder.toString(),
                Duration.between(requestLogBuilder.getRequestTimeStamp(),
                        responseLogBuilder.getResponseTimeStamp())
                        /*
                         * 1000000D: Without D it does a integer division and the precision is lost
                         * Note: Java does not have a get(millisec-tem[poral) as of now, only nano
                         * or sec precision is supported
                         */
                        .getNano()/1000000D)
        );
    }

}
