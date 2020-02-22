package org.jsmart.zerocode.core.logbuilder;

import java.time.LocalDateTime;

public class ResponseLogBuilder {
    String relationshipId;
    LocalDateTime responseTimeStamp;
    String response;
    String exceptionMsg;
    String assertion = "{Oops! Not decided. Possibly due to non JSON content was encountered. See log for details}";
    String customLog;


    public ResponseLogBuilder customLog(String customLog) {
        this.customLog = customLog;
        return this;
    }

    public ResponseLogBuilder relationshipId(String relationshipId) {
        this.relationshipId = relationshipId;
        return this;
    }

    public ResponseLogBuilder responseTimeStamp(LocalDateTime responseTimeStamp) {
        this.responseTimeStamp = responseTimeStamp;
        return this;
    }

    public ResponseLogBuilder response(String response) {
        this.response = response;
        return this;
    }

    public ResponseLogBuilder exceptionMessage(String exceptionMsg) {
        this.exceptionMsg = exceptionMsg;
        return this;
    }

    public LocalDateTime getResponseTimeStamp() {
        return responseTimeStamp;
    }
    public String getResponse() {
		return response;
	}

    @Override
    public String toString() {
        return relationshipId +
                "\nResponse:\n" + response +
                "\n*responseTimeStamp:" + responseTimeStamp;
                //"\n\n---------> Assertion: <----------\n" + assertion;
    }

    public ResponseLogBuilder assertionSection(String assertionJson) {
        this.assertion = assertionJson;
        return this;
    }

    public String getAssertion() {
        return assertion;
    }

    public String getCustomLog() {
        return customLog;
    }

}
