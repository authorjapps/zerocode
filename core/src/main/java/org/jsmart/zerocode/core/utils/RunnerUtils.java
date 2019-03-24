package org.jsmart.zerocode.core.utils;

import org.apache.commons.lang.StringUtils;
import org.jsmart.zerocode.core.domain.EnvProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jsmart.zerocode.core.utils.SmartUtils.getEnvPropertyValue;
import static org.jsmart.zerocode.core.utils.TokenUtils.getTestCaseTokens;

/**
 * This code is used by Unit and Package runner.
 * Hence resides at a common place.
 */
public class RunnerUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(RunnerUtils.class);

    public static String getEnvSpecificConfigFile(String serverEnv, Class<?> testClass) {
        LOGGER.info("### testClass : " + testClass);

        final EnvProperty envProperty = testClass.getAnnotation(EnvProperty.class);

        if(envProperty == null){
            return serverEnv;
        }

        String envPropNameWithPrefix = envProperty.value();

        List<String> allTokens = getTestCaseTokens(envPropNameWithPrefix);

        if(allTokens.size() >= 1 && null != getEnvPropertyValue(allTokens.get(0))){

            final String propertyKey = allTokens.get(0);
            final String propertyValue = getEnvPropertyValue(propertyKey);

            Map<String, String> paramMap = new HashMap<>();
            paramMap.put(propertyKey, propertyValue);

            final String resolvedEnvPropNameWithPrefix = SmartUtils.resolveToken(envPropNameWithPrefix, paramMap);

            serverEnv = suffixEnvValue(serverEnv, resolvedEnvPropNameWithPrefix);

            LOGGER.info("Found env specific property: '{}={}', Hence using: '{}'", propertyKey, propertyValue, serverEnv);

        } else if(allTokens.size() >= 1) {

            final String propertyKey = allTokens.get(0);

            LOGGER.info("Could not find env value for env property '{}', So using '{}'", propertyKey, serverEnv);

        } else {

            LOGGER.info("Could not find env specific property, So using '{}'", serverEnv);

        }

        return serverEnv;
    }

    public static String suffixEnvValue(String serverEnv, String resolvedEnvPropNameWithPrefix) {
        final String DOT_PROPERTIES = ".properties";
        return serverEnv.replace(DOT_PROPERTIES, resolvedEnvPropNameWithPrefix + DOT_PROPERTIES);
    }

    public static String getFullyQualifiedUrl(String serviceEndPoint,
                                              String host,
                                              String port,
                                              String applicationContext) {
        if (serviceEndPoint.startsWith("http://") || serviceEndPoint.startsWith("https://")) {
            return serviceEndPoint;

        } else if(StringUtils.isEmpty(port)){
            return String.format("%s%s%s", host, applicationContext, serviceEndPoint);

        } else {
            /*
             * Make sure your property file contains context-path with a front slash like "/google-map".
             * -OR-
             * Empty context path is also ok if it requires. In this case do not put front slash.
             */
            return String.format("%s:%s%s%s", host, port, applicationContext, serviceEndPoint);
        }
    }

}
