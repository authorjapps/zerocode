package org.jsmart.zerocode.core.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.tomakehurst.wiremock.common.Json;

import java.util.List;

public class SchemaStep extends Step{

    private JsonNode response;
    public SchemaStep(Integer loop, Retry retry, String name, String operation, String method, String url, JsonNode request, List<Validator> validators, JsonNode sort, JsonNode assertions, JsonNode verify , JsonNode response , String verifyMode, boolean ignoreStep) {
        super(loop, retry, name, operation, method, url, request, validators, sort, assertions, verify, verifyMode, ignoreStep);

        this.response = response;
    }

    public JsonNode getResponse()
    {
        return response;
    }

    public void setResponse(JsonNode response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "SchemaStep{" +
                "loop=" + getLoop() +
                ", retry='" + getRetry() + '\'' +
                ", name='" + getName() + '\'' +
                ", method='" + getMethod() + '\'' +
                ", operation='" + getOperation() + '\'' +
                ", url='" + getUrl() + '\'' +
                ", request=" + getRequest() +
                ", validators=" + getValidators() +
                ", assertions=" + getAssertions() +
                ", response=" + response +
                ", verifyMode=" + getVerifyMode() +
                ", verify=" + getVerify() +
                ", id='" + getId() + '\'' +
                ", stepFile=" + getStepFile() +
                ", stepFiles=" + getStepFiles() +
                ", parameterized=" + getParameterized() +
                ", customLog=" + getCustomLog() +
                '}';
    }
}
