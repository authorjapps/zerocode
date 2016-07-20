package org.jsmart.zerocode.core.domain;

public class Body<T> {
    private final T bodyJson;
    private final String bodyString;

    public Body(T bodyJson, String bodyString) {
        this.bodyJson = bodyJson;
        this.bodyString = bodyString;
    }

    public T getBodyJson() {
        return bodyJson;
    }

    public String getBodyString() {
        return bodyString.toString();
    }
}
