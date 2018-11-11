package org.jsmart.zerocode.core.kafka.consume;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;

//@JsonIgnoreProperties(ignoreUnknown = true) //<--- Do not enable this. All properties to be aware of and processed
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ConsumerLocalConfigs {
    private final String fileDumpTo;
    private final String fileDumpType;
    private final Boolean commitAsync;
    private final Boolean commitSync;
    private final Boolean showRecordsInResponse;
    private final Integer maxNoOfRetryPollsOrTimeouts;

    @JsonCreator
    public ConsumerLocalConfigs(
            @JsonProperty("fileDumpTo") String fileDumpTo,
            @JsonProperty("fileDumpType") String fileDumpType,
            @JsonProperty("commitAsync") Boolean commitAsync,
            @JsonProperty("commitSync") Boolean commitSync,
            @JsonProperty("showRecordsInResponse") Boolean showRecordsInResponse,
            @JsonProperty("maxNoOfRetryPollsOrTimeouts") Integer maxNoOfRetryPollsOrTimeouts) {
        this.fileDumpTo = fileDumpTo;
        this.fileDumpType = fileDumpType;
        this.commitAsync = commitAsync;
        this.commitSync = commitSync;
        this.showRecordsInResponse = showRecordsInResponse;
        this.maxNoOfRetryPollsOrTimeouts = maxNoOfRetryPollsOrTimeouts;
    }

    ConsumerLocalConfigs() {
        this(null, null, null, null, null, null);
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

    public Boolean getShowRecordsInResponse() {
        return showRecordsInResponse;
    }

    public Integer getMaxNoOfRetryPollsOrTimeouts() {
        return maxNoOfRetryPollsOrTimeouts;
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
                Objects.equals(showRecordsInResponse, that.showRecordsInResponse) &&
                Objects.equals(maxNoOfRetryPollsOrTimeouts, that.maxNoOfRetryPollsOrTimeouts);
    }

    @Override
    public int hashCode() {

        return Objects.hash(fileDumpTo, fileDumpType, commitAsync, commitSync, showRecordsInResponse, maxNoOfRetryPollsOrTimeouts);
    }

    @Override
    public String toString() {
        return "ConsumerLocalConfigs{" +
                "fileDumpTo='" + fileDumpTo + '\'' +
                ", fileDumpType='" + fileDumpType + '\'' +
                ", commitAsync=" + commitAsync +
                ", commitSync=" + commitSync +
                ", showRecordsInResponse=" + showRecordsInResponse +
                ", maxNoOfRetryPollsOrTimeouts=" + maxNoOfRetryPollsOrTimeouts +
                '}';
    }
}
