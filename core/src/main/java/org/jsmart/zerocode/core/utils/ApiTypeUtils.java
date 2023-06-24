package org.jsmart.zerocode.core.utils;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.jsmart.zerocode.core.constants.ZerocodeConstants.KAFKA;

public class ApiTypeUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiTypeUtils.class);

    public static final String JAVA_API_PROTOCOL_MAPPINGS = "java.api.protocol.mappings";

    @Inject(optional = true)
    @Named(JAVA_API_PROTOCOL_MAPPINGS)
    private String javaApiProtoMappings;

    public ApiTypeUtils() {
    }

    public ApiTypeUtils(String javaApiProtoMappings) {
        this.javaApiProtoMappings = javaApiProtoMappings;
    }

    public static ApiType apiType(String serviceName, String methodName) {
        ApiType apiType;

        if (StringUtils.isEmpty(serviceName) || isEmpty(methodName)) {
            apiType = ApiType.NONE;

        } else if (serviceName.contains("://") && !serviceName.startsWith("http")) {
            apiType = ApiType.JAVA_CALL;

        } else if (serviceName != null && serviceName.contains("/")) {
            apiType = ApiType.REST_CALL;

        } else if (serviceName != null && serviceName.contains(KAFKA)) {
            apiType = ApiType.KAFKA_CALL;

        } else {
            apiType = ApiType.JAVA_CALL;

        }

        return apiType;
    }

    public String getQualifiedJavaApi(String url) {
        if (!url.contains("://")){
            return url;
        }
        return findMapping(javaApiProtoMappings, url);
    }

    private String findMapping(String javaApiProtoMappings, String url) {
        LOGGER.debug("Locating protocol service mapping for - '{}'", url);

        if (isEmpty(javaApiProtoMappings)) {
            LOGGER.error("Protocol mapping was null or empty. Please create the mappings first and then rerun");
            throw new RuntimeException("\nProtocol mapping was null or empty.");
        }
        List<String> mappingList = Arrays.asList(javaApiProtoMappings.split(","));
        String foundMapping = mappingList.stream()
                .filter(thisMapping -> thisMapping.startsWith(url))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("\nurl '" + url + "' Not found"));

        String qualifiedClazz = foundMapping.split("\\|")[1];
        LOGGER.debug("Found protocol mapping for - '{} -> {}'", url, qualifiedClazz);

        return qualifiedClazz;
    }
}
