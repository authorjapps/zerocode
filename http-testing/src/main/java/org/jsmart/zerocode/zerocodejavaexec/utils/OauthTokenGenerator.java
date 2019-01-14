package org.jsmart.zerocode.zerocodejavaexec.utils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class OauthTokenGenerator {

    public Map<String, String> generateToken(String anyParam){
        Map<String, String> tokenMap = new HashMap<>();

        // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
        // This is for demo only.
        // You need to put a valid implementation for this token.
        // For time being it's unique here as current time-stamp.
        // The key "token" here is just for demo purpose.
        // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
        String uniqueToken = LocalDateTime.now().toString()
                .replace(":", "-")
                .replace(".", "-");

        tokenMap.put("token", uniqueToken);

        return tokenMap;
    }
}
