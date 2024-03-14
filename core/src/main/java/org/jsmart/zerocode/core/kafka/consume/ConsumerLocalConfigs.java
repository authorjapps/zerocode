package org.jsmart.zerocode.core.kafka.consume;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

import static org.jsmart.zerocode.core.kafka.KafkaConstants.RAW;

//@JsonIgnoreProperties(ignoreUnknown = true) //<--- Do not enable this. All properties need to be aware of and processed
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsumerLocalConfigs {
    private final String recordType;
    private final String fileDumpTo;
    private final Boolean commitAsync;
    private final Boolean commitSync;
    private final Boolean showRecordsConsumed;
    private final Integer maxNoOfRetryPollsOrTimeouts;
    private final Long pollingTime;
    private final String seek;
    private final String protoClassType;
    private final Boolean cacheByTopic;
    private final String filterByJsonPath;
    private final String seekEpoch;
    private final SeekTimestamp seekTimestamp;


    @JsonCreator
    public ConsumerLocalConfigs(
            @JsonProperty("recordType") String recordType,
            @JsonProperty("protoClassType") String protobufMessageClassType,
            @JsonProperty("fileDumpTo") String fileDumpTo,
            @JsonProperty("commitAsync") Boolean commitAsync,
            @JsonProperty("commitSync") Boolean commitSync,
            @JsonProperty("showRecordsConsumed") Boolean showRecordsConsumed,
            @JsonProperty("maxNoOfRetryPollsOrTimeouts") Integer maxNoOfRetryPollsOrTimeouts,
            @JsonProperty("pollingTime") Long pollingTime,
            @JsonProperty("cacheByTopic") Boolean cacheByTopic,
            @JsonProperty("filterByJsonPath") String filterByJsonPath,
            @JsonProperty("seek") String seek,
            @JsonProperty("seekEpoch") String seekEpoch,
            @JsonProperty("seekTimestamp") SeekTimestamp seekTimestamp) {
        this.recordType = recordType;
        this.protoClassType = protobufMessageClassType;
        this.fileDumpTo = fileDumpTo;
        this.commitAsync = commitAsync;
        this.commitSync = commitSync;
        this.showRecordsConsumed = showRecordsConsumed;
        this.maxNoOfRetryPollsOrTimeouts = maxNoOfRetryPollsOrTimeouts;
        this.pollingTime = pollingTime;
        this.cacheByTopic = cacheByTopic;
        this.filterByJsonPath = filterByJsonPath;
        this.seek = seek;
        this.seekEpoch = seekEpoch;
        this.seekTimestamp = seekTimestamp;
    }


    public ConsumerLocalConfigs(
            String recordType,
            String fileDumpTo,
            Boolean commitAsync,
            Boolean commitSync,
            Boolean showRecordsConsumed,
            Integer maxNoOfRetryPollsOrTimeouts,
            Long pollingTime,
            Boolean cacheByTopic,
            String filterByJsonPath,
            String seek,
            String seekEpoch,
            SeekTimestamp seekTimestamp) {
        this(recordType, null,
                fileDumpTo,
                commitAsync,
                commitSync,
                showRecordsConsumed,
                maxNoOfRetryPollsOrTimeouts,
                pollingTime,
                cacheByTopic,
                filterByJsonPath,
                seek,
                seekEpoch,
                seekTimestamp);
    }

    public String getRecordType() {
        return recordType != null ? recordType : RAW;
    }

    public String getProtoClassType() {
        return protoClassType;
    }


    public String getFileDumpTo() {
        return fileDumpTo;
    }

    public Boolean getCommitAsync() {
        return commitAsync;
    }

    public Boolean getCommitSync() {
        return commitSync;
    }

    public Boolean getShowRecordsConsumed() {
        return showRecordsConsumed != null ? showRecordsConsumed : true;
    }

    public Integer getMaxNoOfRetryPollsOrTimeouts() {
        return maxNoOfRetryPollsOrTimeouts;
    }

    public Long getPollingTime() {
        return pollingTime;
    }

    public Boolean getCacheByTopic() {
        return cacheByTopic;
    }

    public String getFilterByJsonPath() {
        return filterByJsonPath;
    }

    public String getSeek() {
        return seek;
    }

    public String getSeekEpoch() {
        return seekEpoch;
    }

    public SeekTimestamp getSeekTimestamp() {
        return seekTimestamp;
    }

    @JsonIgnore
    public String[] getSeekTopicPartitionOffset() {
        return seek.split(",");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsumerLocalConfigs that = (ConsumerLocalConfigs) o;
        return Objects.equals(recordType, that.recordType) &&
                Objects.equals(protoClassType, that.protoClassType) &&
                Objects.equals(fileDumpTo, that.fileDumpTo) &&
                Objects.equals(commitAsync, that.commitAsync) &&
                Objects.equals(commitSync, that.commitSync) &&
                Objects.equals(showRecordsConsumed, that.showRecordsConsumed) &&
                Objects.equals(maxNoOfRetryPollsOrTimeouts, that.maxNoOfRetryPollsOrTimeouts) &&
                Objects.equals(pollingTime, that.pollingTime) &&
                Objects.equals(filterByJsonPath, that.filterByJsonPath) &&
                Objects.equals(cacheByTopic, that.cacheByTopic) &&
                Objects.equals(seek, that.seek) &&
                Objects.equals(seekEpoch, that.seekEpoch);
    }

    @Override
    public int hashCode() {

        return Objects.hash(recordType, fileDumpTo, commitAsync, commitSync, showRecordsConsumed, maxNoOfRetryPollsOrTimeouts, pollingTime, cacheByTopic, filterByJsonPath, seek);
    }

    @Override
    public String toString() {
        return "ConsumerLocalConfigs{" +
                "recordType='" + recordType + '\'' +
                "protobufMessageClassType='" + protoClassType + '\'' +
                ", fileDumpTo='" + fileDumpTo + '\'' +
                ", commitAsync=" + commitAsync +
                ", commitSync=" + commitSync +
                ", showRecordsConsumed=" + showRecordsConsumed +
                ", maxNoOfRetryPollsOrTimeouts=" + maxNoOfRetryPollsOrTimeouts +
                ", pollingTime=" + pollingTime +
                ", cacheByTopic=" + cacheByTopic +
                ", filterByJsonPath=" + filterByJsonPath +
                ", seek=" + seek +
                ", seekEpoch=" + seekEpoch +
                ", seekTimestamp=" + seekTimestamp +
                '}';
    }
}
