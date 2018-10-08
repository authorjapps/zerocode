package org.jsmart.zerocode.core.kafka.consume;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ConsumeTestProperties {
    private final String fileDumpTo;
    private final String fileDumpType;
    private final Boolean commitAsync;
    private final Boolean showRecordsAsResponse;

    @JsonCreator
    public ConsumeTestProperties(
            @JsonProperty("fileDumpTo") String fileDumpTo,
            @JsonProperty("fileDumpType") String fileDumpType,
            @JsonProperty("commitAsync") Boolean commitAsync,
            @JsonProperty("showRecordsAsResponse") Boolean showRecordsAsResponse) {
        this.fileDumpTo = fileDumpTo;
        this.fileDumpType = fileDumpType;
        this.commitAsync = commitAsync;
        this.showRecordsAsResponse = showRecordsAsResponse;
    }

    ConsumeTestProperties() {
        this(null, null, null, null);
    }

    public static ConsumeTestProperties empty(){
        return new ConsumeTestProperties();
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

    public Boolean getShowRecordsAsResponse() {
        return showRecordsAsResponse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsumeTestProperties that = (ConsumeTestProperties) o;
        return Objects.equals(fileDumpTo, that.fileDumpTo) &&
                Objects.equals(fileDumpType, that.fileDumpType) &&
                Objects.equals(commitAsync, that.commitAsync) &&
                Objects.equals(showRecordsAsResponse, that.showRecordsAsResponse);
    }

    @Override
    public int hashCode() {

        return Objects.hash(fileDumpTo, fileDumpType, commitAsync, showRecordsAsResponse);
    }

    @Override
    public String toString() {
        return "ConsumeTestProperties{" +
                "fileDumpTo='" + fileDumpTo + '\'' +
                ", fileDumpType='" + fileDumpType + '\'' +
                ", commitAsync=" + commitAsync +
                ", showRecordsAsResponse=" + showRecordsAsResponse +
                '}';
    }
}
