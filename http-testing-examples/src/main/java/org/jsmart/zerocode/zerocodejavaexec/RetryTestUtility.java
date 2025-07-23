package org.jsmart.zerocode.zerocodejavaexec;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class RetryTestUtility {

    private static int helper1Count = 0;
    private static int helper2Count = 0;
    private static int helper3Count = 0;

    public static Integer helperMethod1() {
        helper1Count++;
        return helper1Count;
    }

    public static Integer helperMethod2() {
        if (helper1Count > helper2Count)
            helper2Count++;
        return helper2Count;
    }

    public static Integer helperMethod3() {
        if (helper2Count > helper3Count)
            helper3Count++;
        return helper3Count;
    }

    public Map<String, String> mainMethod(int requirement) {
        boolean result = helper1Count >= requirement && helper2Count >= requirement && helper3Count >= requirement;
        if (result) {
            helper1Count = 0;
            helper2Count = 0;
            helper3Count = 0;
        }
        return ImmutableMap.of("success", String.valueOf(result));
    }
}
