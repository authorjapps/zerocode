package org.jsmart.zerocode.zerocodejavaexec;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class SampleMethods {

    public static class SamplePOJO {
        public String arg1;
        public String arg2;

        @JsonCreator
        public SamplePOJO(@JsonProperty("arg1") String arg1, @JsonProperty("arg2") String arg2) {
            this.arg1 = arg1;
            this.arg2 = arg2;
        }
    }

    public static Map<String, String> sampleStaticMethod(String arg1, String arg2) {
        return ImmutableMap.of("0", arg1,
                "1", arg2);
    }

    public Map<String, String> sampleMultiArgMethod1(String arg1, String arg2) {
        return ImmutableMap.of("0", arg1,
                "1", arg2);
    }

    public Map<String, Object> sampleMultiArgMethod2(String arg1, Map<String, String> arg2) {
        return ImmutableMap.of("0", arg1,
                "1", arg2);
    }

    public Map<String, Object> sampleMultiArgMethod3(String arg1, SamplePOJO arg2) {
        return ImmutableMap.of("0", arg1,
                "1", arg2);
    }
}
