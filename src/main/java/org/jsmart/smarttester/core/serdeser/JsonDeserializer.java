//package org.jsmart.smarttester.core.serdeser;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonElement;
//
//public class JsonDeserializer {
//
//    private static Gson gson;
//
//    static {
//        GsonBuilder builder = new GsonBuilder();
//        builder.setPrettyPrinting();
//
//        //TODO: nirmal
//        // builder.registerTypeAdapter(RESTRequest.class, new RESTRequestDeserializer(RESTRequest.class));
//        //builder.registerTypeHierarchyAdapter(Referral.class, new ReferralDeserializer(Referral.class));
//
//        gson = builder.create();
//    }
//
//    public static <T> T deserialize(String jsonString, Class<T> type) {
//        return gson.fromJson(jsonString, type);
//    }
//
//    public static <T> T deserialize(JsonElement element, Class<T> type) {
//        return gson.fromJson(element, type);
//    }
//
//
//}
//
