package org.jsmart.zerocode.core.utils;

import org.apache.commons.lang.StringUtils;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.jsmart.zerocode.core.constants.ZerocodeConstants.KAFKA;

public class ApiTypeUtils {

    public static ApiType apiType(String serviceName, String methodName) {
        ApiType apiType;

        if (StringUtils.isEmpty(serviceName) || isEmpty(methodName)) {
            apiType = ApiType.NONE;

        } else if (serviceName != null && serviceName.contains("/")) {
            apiType = ApiType.REST_CALL;

        } else if (serviceName != null && serviceName.contains(KAFKA)) {
            apiType = ApiType.KAFKA_CALL;

        } else {
            apiType = ApiType.JAVA_CALL;

        }

        return apiType;
    }

}
