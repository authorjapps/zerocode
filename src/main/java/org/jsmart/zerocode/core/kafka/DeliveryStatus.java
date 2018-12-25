package org.jsmart.zerocode.core.kafka;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DeliveryStatus {
    private final String status;
    private final String message;
    private final Integer recordCount;

    @JsonCreator
    public DeliveryStatus(
            @JsonProperty("status") String status,
            @JsonProperty("message") String message,
            @JsonProperty("recordCount") Integer recordCount) {
        this.status = status;
        this.message = message;
        this.recordCount = recordCount;
    }

    public DeliveryStatus(String status, String message) {
        this(status, message, null);
    }
    public DeliveryStatus(String status, Integer recordCount) {
        this(status, null, recordCount);
    }

    public DeliveryStatus(String status) {
        this(status, null, null);
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Integer getRecordCount() {
        return recordCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeliveryStatus that = (DeliveryStatus) o;
        return Objects.equals(status, that.status) &&
                Objects.equals(message, that.message) &&
                Objects.equals(recordCount, that.recordCount);
    }

    @Override
    public int hashCode() {

        return Objects.hash(status, message, recordCount);
    }

    @Override
    public String toString() {
        return "DeliveryStatus{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", recordCount='" + recordCount + '\'' +
                '}';
    }
}
