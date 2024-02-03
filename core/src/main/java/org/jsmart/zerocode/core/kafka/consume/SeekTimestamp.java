package org.jsmart.zerocode.core.kafka.consume;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
@AllArgsConstructor
public class SeekTimestamp {

    private final String timestamp;
    private final String format;
}
