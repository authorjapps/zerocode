package org.jsmart.zerocode.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MockSteps {

    private final List<MockStep> mocks;

    @JsonCreator
    public MockSteps(@JsonProperty("mocks") List<MockStep> mocks) {
        this.mocks = mocks;
    }

    public List<MockStep> getMocks() {
        return mocks == null ? (new ArrayList<>()) : mocks;
    }

    @Override
    public String toString() {
        return "MockSteps{" +
                "mocks=" + mocks +
                '}';
    }
}
