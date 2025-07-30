package org.jsmart.zerocode.zerocodejavaexec.utils;

import org.jsmart.zerocode.core.kafka.consume.SeekTimestamp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ExampleUtils {

    public String seekTimestampToEpoch(SeekTimestamp seekTimestamp) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(seekTimestamp.getFormat());
        return String.valueOf(dateFormat.parse(seekTimestamp.getTimestamp()).toInstant().toEpochMilli());
    }

}
