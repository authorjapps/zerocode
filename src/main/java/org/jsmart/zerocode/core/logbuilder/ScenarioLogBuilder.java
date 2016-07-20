package org.jsmart.zerocode.core.logbuilder;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.time.LocalDateTime;

public class ScenarioLogBuilder {

    String name;
    LocalDateTime requestTimeStamp;

    @JsonCreator
    public ScenarioLogBuilder() {
    }


    public LocalDateTime getRequestTimeStamp() {
        return requestTimeStamp;
    }

    @Override
    public String toString() {
        return "\n-------------------------- Scenario: ----------------------------\n" +
                "\nname:" + name +
                "\nrequestTimeStamp:" + requestTimeStamp;
    }


    public ScenarioLogBuilder scenarioName(String name) {
        this.name = name;
        return this;
    }

    public ScenarioLogBuilder requestTimeStamp(LocalDateTime now) {
        this.requestTimeStamp = now;
        return this;
    }
}
