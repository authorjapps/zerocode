package org.jsmart.zerocode.zerocodejavaexec.utils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class TokenGenerator {

    public Map<String, String> generateNew(String anyParam){
        Map<String, String> tokenMap = new HashMap<>();

        // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
        //          Your token generation logic goes here.
        // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
        // You need to put a valid implementation for this token.
        // For time being it's unique here as current time-stamp.
        // The key "token" here is just for demo purpose.
        // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
        String uniqueToken = tokenFromCurrentTimeStamp();

        // ------------------------------------
        // Now put this token into a map key
        //  - Choose any key name e.g. newToken
        // ------------------------------------
        tokenMap.put("newToken", uniqueToken);

        return tokenMap;
    }

    private String tokenFromCurrentTimeStamp() {
        return LocalDateTime.now().toString()
                    .replace(":", "-")
                    .replace(".", "-");
    }
}
