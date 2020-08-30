//package org.jsmart.zerocode.core.engine.executor.javaapi;
//
//import java.util.Map;
//
//public class MyCustomComparator implements CustomAsserter { //implement an Interface with method strLength(Map<String, Object> inputMap, Object actual)
//    // ---------------------------
//    // "public static" also works
//    // ---------------------------
//    public Boolean strLength(Map<String, Object> inputParamMap, Object actualFieldValue) {
//        int expectLen = Integer.parseInt(inputParamMap.get("expectLen").toString());
//        return expectLen == actualFieldValue.toString().length();
//    }
//
//    public static Integer strLen(String input) {
//        return 6;
//    }
//
//    public static Integer strLen(String input1, String inut2) {
//        return 7;
//    }
//
//}
