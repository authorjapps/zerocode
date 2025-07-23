package org.jsmart.zerocode.zerocodejavaexec.utils;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.util.HashMap;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public class PropertyUtils {
    private static final org.slf4j.Logger LOGGER = getLogger(PropertyUtils.class);
    public static final String PROPERTY_HOST = "restful.application.endpoint.host";
    public static final String PROPERTY_PORT = "restful.application.endpoint.port";
    public static final String ANOTHER_NEW_ENDPOINT_HOST = "another.new.endpoint.host";
    public static final String NEW_API_HOST = "new_api_host";

    @Inject(optional = true)
    @Named(PROPERTY_HOST)
    private String restfulHost;

    @Inject(optional = true)
    @Named(PROPERTY_PORT)
    private Integer restfulPort;

    @Inject(optional = true)
    @Named(ANOTHER_NEW_ENDPOINT_HOST)
    private String aNewHost;

    @Inject(optional = true)
    @Named(NEW_API_HOST)
    private String newApiHost;

    // -----------------------------------------------------------
    // You can @Inject any property from the config here via
    // @Named("property key name from config file") as shown above
    // -----------------------------------------------------------
    public Map<String, Object> readProperties(String optionalString) {

        Map<String, Object> propertiesMap = new HashMap<>();
        propertiesMap.put(PROPERTY_HOST, restfulHost);
        propertiesMap.put(PROPERTY_PORT, restfulPort);
        propertiesMap.put(ANOTHER_NEW_ENDPOINT_HOST, aNewHost);
        propertiesMap.put(NEW_API_HOST, newApiHost);

        return propertiesMap;
    }

}
