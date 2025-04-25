package org.jsmart.zerocode.zerocodejavaexec.utils;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.util.HashMap;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public class HostConfigs {
    private static final org.slf4j.Logger LOGGER = getLogger(HostConfigs.class);
    public static final String NEW_API_HOST = "new_api_host";

    @Inject(optional = true)
    @Named(NEW_API_HOST)
    private String newApiHost;

    // -----------------------------------------------------------
    // You can @Inject any property from the config here via
    // @Named("property key name from config file") as shown above
    // -----------------------------------------------------------
    public Map<String, Object> readProperties(String optionalString) {

        Map<String, Object> propertiesMap = new HashMap<>();
        propertiesMap.put(NEW_API_HOST, newApiHost);

        return propertiesMap;
    }

}
