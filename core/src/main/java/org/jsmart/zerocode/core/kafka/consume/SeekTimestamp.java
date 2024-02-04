package org.jsmart.zerocode.core.kafka.consume;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;


@Getter
@Builder
@ToString
@Jacksonized
public class SeekTimestamp {

    private final String timestamp;
    private final String format;
}
