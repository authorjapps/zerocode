package org.jsmart.zerocode.core.domain;

import java.util.List;

public class Retry {
    private Integer max;
    private Integer delay;
    private List<String> withSteps;

    public Integer getMax() {
        return max;
    }

    public Integer getDelay() {
        return delay;
    }

    public List<String> getWithSteps() {
        return withSteps;
    }
    public Retry() {}

    public Retry(Integer max, Integer delay, List<String> withSteps) {
        this.max = max;
        this.delay = delay;
        this.withSteps = withSteps;
    }


}
