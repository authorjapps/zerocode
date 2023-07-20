package org.jsmart.zerocode.core.logbuilder;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.time.LocalDateTime;

public class RequestLogBuilder {

    private String relationshipId;
    private LocalDateTime requestTimeStamp;
    private String url;
    private String method;
    private String request;
    private String stepName;
    private Integer loop;
    private String id;


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

    public RequestLogBuilder id(String id){
        this.id = id;
        return this;
    }


    public LocalDateTime getRequestTimeStamp() {
        return requestTimeStamp;
    }

    public String getRelationshipId() {
        return relationshipId;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public String getRequest() {
        return request;
    }

    public String getStepName() {
        return stepName;
    }

    public Integer getLoop() {
        return loop;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    @Override
    public String toString() {
        return relationshipId +
                "\n*requestTimeStamp:" + requestTimeStamp +
                "\nstep:" + stepName +
                "\nid:" + (id != null? id : "None") +
                "\nurl:" + url +
                "\nmethod:" + method +
                "\nrequest:\n" + request;
    }


}
