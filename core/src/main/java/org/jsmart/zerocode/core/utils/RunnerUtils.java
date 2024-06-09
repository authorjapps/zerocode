package org.jsmart.zerocode.core.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.jsmart.zerocode.core.domain.EnvProperty;
import org.jsmart.zerocode.core.domain.Parameterized;
import org.jsmart.zerocode.core.domain.Step;
import org.jsmart.zerocode.core.domain.TestMapping;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.System.getProperty;
import static org.jsmart.zerocode.core.constants.ZeroCodeReportConstants.CHARTS_AND_CSV;
import static org.jsmart.zerocode.core.constants.ZeroCodeReportConstants.ZEROCODE_JUNIT;
import static org.jsmart.zerocode.core.utils.SmartUtils.getEnvPropertyValue;
import static org.jsmart.zerocode.core.utils.TokenUtils.getTestCaseTokens;

/**
 * This code is used by Unit and Package runner.
 * Hence resides at a common place.
 */
public class RunnerUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(RunnerUtils.class);
    public static final int MIN_COUNT = 1;

    public static String getEnvSpecificConfigFile(String serverEnv, Class<?> testClass) {
        LOGGER.debug("### testClass : " + testClass);

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

            LOGGER.debug("Found env specific property: '{}={}', Hence using: '{}'", propertyKey, propertyValue, serverEnv);

        } else if(allTokens.size() >= 1) {

            final String propertyKey = allTokens.get(0);

            LOGGER.warn("Could not find env value for env property '{}', So using '{}'", propertyKey, serverEnv);

        } else {

            LOGGER.warn("Could not find env specific property, So using '{}'", serverEnv);

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

    public static void validateTestMethod(Class<?> testClass) {
        String errMessage = " was invalid, please re-check and pick the correct test method to load.";
        try {
            TestMapping methodMapping = testClass.getAnnotation(TestMapping.class);
            errMessage = "Mapped test method `" + methodMapping.testMethod() + "`" + errMessage;
            methodMapping.testClass().getMethod(methodMapping.testMethod());
        } catch (NoSuchMethodException e) {
            LOGGER.error(errMessage);
            throw new RuntimeException(errMessage + e);
        }
    }

    public static int loopCount(Step thisStep) {
        int stepLoopTimes = 0;

        if(thisStep.getLoop() != null){
            stepLoopTimes = thisStep.getLoop();
        } else if(thisStep.getParameterized() != null){
            stepLoopTimes = thisStep.getParameterized().size();
        } else if(thisStep.getParameterizedCsv() != null){
            stepLoopTimes = thisStep.getParameterizedCsv().size();
        }

        return stepLoopTimes > 0 ? stepLoopTimes: MIN_COUNT;
    }

    public static void handleTestCompleted(RunListener reportListener, Logger logger) {
        if (CHARTS_AND_CSV.equals(getProperty(ZEROCODE_JUNIT))) {
            /**
             * Gradle does not support JUnit RunListener. Hence Zerocode gracefully handled this
             * upon request from Gradle users. But this is not limited to Gradle, anywhere you
             * want to bypass the JUnit RunListener, you can achieve this way.
             * See README for details.
             *
             * There are number of tickets opened for this, but not yet fixed.
             * - https://discuss.gradle.org/t/testrunfinished-not-run-in-junit-integration/14644
             * - https://github.com/gradle/gradle/issues/842
             * - many more related tickets.
             */
            logger.debug("Bypassed JUnit RunListener [as configured by the build tool] to generate useful reports...");
            try {
                reportListener.testRunFinished(new Result());
            } catch (Exception e) {
                logger.error("### Exception occurred while handling non-maven(e.g. Gradle) report generation => " + e);
                throw new RuntimeException(e);
            }
        }
    }
}
