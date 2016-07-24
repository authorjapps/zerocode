package org.jsmart.zerocode.core.logbuilder;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.time.LocalDateTime;

public class RequestLogBuilder {

    String relationshipId;
    LocalDateTime requestTimeStamp;
    String url;
    String method;
    String request;
    String stepName;
    Integer loop;

    @JsonCreator
    public RequestLogBuilder() {
    }

    public RequestLogBuilder stepLoop(Integer loop) {
        this.loop = loop;
        return this;
    }

    public RequestLogBuilder relationshipId(String relationshipId) {
        this.relationshipId = relationshipId;
        return this;
    }

    public RequestLogBuilder requestTimeStamp(LocalDateTime requestTimeStamp) {
        this.requestTimeStamp = requestTimeStamp;
        return  this;
    }

    public RequestLogBuilder url(String url) {
        this.url = url;
        return this;
    }

    public RequestLogBuilder method(String method) {
        this.method = method;
        return this;
    }

    public RequestLogBuilder request(String request) {
        this.request = request;
        return this;
    }

    public RequestLogBuilder step(String stepName) {
        this.stepName = stepName;
        return this;
    }

    public LocalDateTime getRequestTimeStamp() {
        return requestTimeStamp;
    }

    @Override
    public String toString() {
        return relationshipId +
                "\nrequestTimeStamp:" + requestTimeStamp +
                "\nstep:" + stepName +
                "\nurl:" + url +
                "\nmethod:" + method +
                "\nrequest:\n" + request;
    }


}
