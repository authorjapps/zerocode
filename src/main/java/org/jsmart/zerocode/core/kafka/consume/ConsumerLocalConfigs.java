package org.jsmart.zerocode.core.kafka.consume;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ConsumerLocalConfigs {
    private final String fileDumpTo;
    private final String fileDumpType;
    private final Boolean commitAsync;
    private final Boolean commitSync;
    private final Boolean showRecordsAsResponse;

    @JsonCreator
    public ConsumerLocalConfigs(
            @JsonProperty("fileDumpTo") String fileDumpTo,
            @JsonProperty("fileDumpType") String fileDumpType,
            @JsonProperty("commitAsync") Boolean commitAsync,
            @JsonProperty("commitSync") Boolean commitSync,
            @JsonProperty("showRecordsAsResponse") Boolean showRecordsAsResponse) {
        this.fileDumpTo = fileDumpTo;
        this.fileDumpType = fileDumpType;
        this.commitAsync = commitAsync;
        this.commitSync = commitSync;
        this.showRecordsAsResponse = showRecordsAsResponse;
    }

    ConsumerLocalConfigs() {
        this(null, null, null, null, null);
    }

    public static ConsumerLocalConfigs empty(){
        return new ConsumerLocalConfigs();
    }

    public String getFileDumpTo() {
        return fileDumpTo;
    }

    public String getFileDumpType() {
        return fileDumpType;
    }

    public Boolean getCommitAsync() {
        return commitAsync;
    }

    public Boolean getCommitSync() {
        return commitSync;
    }

    public Boolean getShowRecordsAsResponse() {
        return showRecordsAsResponse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsumerLocalConfigs that = (ConsumerLocalConfigs) o;
        return Objects.equals(fileDumpTo, that.fileDumpTo) &&
                Objects.equals(fileDumpType, that.fileDumpType) &&
                Objects.equals(commitAsync, that.commitAsync) &&
                Objects.equals(commitSync, that.commitSync) &&
                Objects.equals(showRecordsAsResponse, that.showRecordsAsResponse);
    }

    @Override
    public int hashCode() {

        return Objects.hash(fileDumpTo, fileDumpType, commitAsync, commitSync, showRecordsAsResponse);
    }

    @Override
    public String toString() {
        return "ConsumerLocalConfigs{" +
                "fileDumpTo='" + fileDumpTo + '\'' +
                ", fileDumpType='" + fileDumpType + '\'' +
                ", commitAsync=" + commitAsync +
                ", commitSync=" + commitSync +
                ", showRecordsAsResponse=" + showRecordsAsResponse +
                '}';
    }
}
