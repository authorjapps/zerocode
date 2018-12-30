package org.jsmart.zerocode.core.kafka.receive;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class ConsumerCommonConfigs {

    @Inject(optional = true)
    @Named("consumer.commitSync")
    private Boolean commitSync;

    @Inject(optional = true)
    @Named("consumer.commitAsync")
    private Boolean commitAsync;

    @Inject(optional = true)
    @Named("consumer.fileDumpTo")
    private String fileDumpTo;

    @Inject(optional = true)
    @Named("consumer.fileDumpType")
    private String fileDumpType;

    @Inject(optional = true)
    @Named("consumer.showConsumedRecords")
    private Boolean showConsumedRecords;

    @Inject(optional = true)
    @Named("consumer.maxNoOfRetryPollsOrTimeouts")
    private Integer maxNoOfRetryPollsOrTimeouts;

    @Inject(optional = true)
    @Named("consumer.pollingTime")
    private Long pollingTime;

    public ConsumerCommonConfigs() {
    }

    public ConsumerCommonConfigs(Boolean commitSync,
                                 Boolean commitAsync,
                                 String fileDumpTo,
                                 String fileDumpType,
                                 Boolean showConsumedRecords,
                                 Integer maxNoOfRetryPollsOrTimeouts,
                                 Long pollingTime

    ) {
        this.commitSync = commitSync;
        this.commitAsync = commitAsync;
        this.fileDumpTo = fileDumpTo;
        this.fileDumpType = fileDumpType;
        this.showConsumedRecords = showConsumedRecords;
        this.maxNoOfRetryPollsOrTimeouts = maxNoOfRetryPollsOrTimeouts;
        this.pollingTime = pollingTime;
    }

    public Boolean getCommitSync() {
        return commitSync;
    }

    public Boolean getCommitAsync() {
        return commitAsync;
    }

    public String getFileDumpTo() {
        return fileDumpTo;
    }

    public String getFileDumpType() {
        return fileDumpType;
    }

    public Boolean getShowConsumedRecords() {
        return showConsumedRecords;
    }

    public Integer getMaxNoOfRetryPollsOrTimeouts() {
        return maxNoOfRetryPollsOrTimeouts;
    }

    public Long getPollingTime() {
        return pollingTime;
    }

    @Override
    public String toString() {
        return "ConsumerCommonConfigs{" +
                "commitSync=" + commitSync +
                ", commitAsync=" + commitAsync +
                ", fileDumpTo='" + fileDumpTo + '\'' +
                ", fileDumpType='" + fileDumpType + '\'' +
                ", showConsumedRecords=" + showConsumedRecords +
                ", maxNoOfRetryPollsOrTimeouts=" + maxNoOfRetryPollsOrTimeouts +
                ", pollingTime=" + pollingTime +
                '}';
    }
}
