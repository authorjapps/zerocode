package org.jsmart.zerocode.core.domain;

public class Retry {
    private Integer max;
    private Integer delay;

    public Integer getMax() {
        return max;
    }

    public Integer getDelay() {
        return delay;
    }

    public Retry() {}

    public Retry(Integer max, Integer delay) {
        this.max = max;
        this.delay = delay;
    }
}
