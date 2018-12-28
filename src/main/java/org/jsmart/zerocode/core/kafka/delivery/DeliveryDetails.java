package org.jsmart.zerocode.core.kafka.delivery;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Objects;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DeliveryDetails {
    private final String status;
    private final String message;
    private final Integer recordCount;
    private final RecordMetadata recordMetadata;

    @JsonCreator
    public DeliveryDetails(
            @JsonProperty("status") String status,
            @JsonProperty("message") String message,
            @JsonProperty("recordCount") Integer recordCount, RecordMetadata recordMetadata) {
        this.status = status;
        this.message = message;
        this.recordCount = recordCount;
        this.recordMetadata = recordMetadata;
    }

    public DeliveryDetails(String status, String message) {
        this(status, message, null, null);
    }
    public DeliveryDetails(String status, RecordMetadata recordMetadata) {
        this(status, null, null, recordMetadata);
    }
    public DeliveryDetails(String status, Integer recordCount) {
        this(status, null, recordCount, null);
    }

    public DeliveryDetails(String status) {
        this(status, null, null, null);
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
        DeliveryDetails that = (DeliveryDetails) o;
        return Objects.equals(status, that.status) &&
                Objects.equals(message, that.message) &&
                Objects.equals(recordCount, that.recordCount) &&
                Objects.equals(recordMetadata, that.recordMetadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, message, recordCount, recordMetadata);
    }

    @Override
    public String toString() {
        return "DeliveryDetails{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", recordCount=" + recordCount +
                ", recordMetadata=" + recordMetadata +
                '}';
    }
}
