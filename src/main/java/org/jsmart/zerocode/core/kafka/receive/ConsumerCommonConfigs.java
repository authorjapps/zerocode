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
    @Named("consumer.showRecordsAsResponse")
    private Boolean showRecordsAsResponse;

    public ConsumerCommonConfigs() {
    }

    public ConsumerCommonConfigs(Boolean commitSync,
                                 Boolean commitAsync,
                                 String fileDumpTo,
                                 String fileDumpType,
                                 Boolean showRecordsAsResponse) {
        this.commitSync = commitSync;
        this.commitAsync = commitAsync;
        this.fileDumpTo = fileDumpTo;
        this.fileDumpType = fileDumpType;
        this.showRecordsAsResponse = showRecordsAsResponse;
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

    public Boolean getShowRecordsAsResponse() {
        return showRecordsAsResponse;
    }

    @Override
    public String toString() {
        return "ConsumerCommonConfigs{" +
                "commitSync=" + commitSync +
                ", commitAsync=" + commitAsync +
                ", fileDumpTo='" + fileDumpTo + '\'' +
                ", fileDumpType='" + fileDumpType + '\'' +
                ", showRecordsAsResponse=" + showRecordsAsResponse +
                '}';
    }
}
