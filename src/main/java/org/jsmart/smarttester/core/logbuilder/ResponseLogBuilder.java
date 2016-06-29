package org.jsmart.smarttester.core.logbuilder;

import java.time.LocalDateTime;

public class ResponseLogBuilder {
    String relationshipId;
    LocalDateTime responseTimeStamp;
    String response;


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

    public LocalDateTime getResponseTimeStamp() {
        return responseTimeStamp;
    }

    @Override
    public String toString() {
        return relationshipId +
                "\nResponse:\n" + response +
                "\nresponseTimeStamp:" + responseTimeStamp;
    }

}
