//package org.jsmart.smarttester.core.serdeser;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonElement;
//
//public class JsonSerializer {
//
//    private static Gson gson;
//
//    static {
//        GsonBuilder builder = new GsonBuilder();
//        builder.setPrettyPrinting();
//
//        gson = builder.create();
//    }
//
//
//    public static String serialize(Object object) {
//
//        return gson.toJson(toJsonTree(object));
//    }
//
//    public static JsonElement toJsonTree(Object object) {
//        return gson.toJsonTree(object);
//    }
//
//}