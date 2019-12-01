package org.jsmart.zerocode.core.domain.builders;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.jsmart.zerocode.core.constants.ZeroCodeReportConstants.REPORT_DISPLAY_NAME_DEFAULT;
import static org.jsmart.zerocode.core.constants.ZeroCodeReportConstants.REPORT_TITLE_DEFAULT;
import static org.slf4j.LoggerFactory.getLogger;

public class ExtentReportsFactory {
    private static final org.slf4j.Logger LOGGER = getLogger(ExtentReportsFactory.class);

    private static ExtentHtmlReporter extentHtmlReporter;

    private static ExtentReports extentReports;

    private static Map<Object, String> systemProperties = new HashMap<>();

    public static ExtentReports createReportTheme(String reportFileName) {
        ExtentHtmlReporter extentHtmlReporter = createExtentHtmlReporter(reportFileName);

        extentReports = new ExtentReports();

        attachSystemInfo();

        extentReports.attachReporter(extentHtmlReporter);
        extentReports.setReportUsesManualConfiguration(true);

        return extentReports;
    }

    public static void attachSystemInfo() {	
        systemProperties = getSystemProperties();
        final String osName = systemProperties.get("os.name");
        final String osArchitecture = systemProperties.get("os.arch");
        final String javaVersion = systemProperties.get("java.version");
        final String javaVendor = systemProperties.get("java.vendor");

        LOGGER.info("Where were the tests fired? Ans: OS:{}, Architecture:{}, Java:{}, Vendor:{}",
                osName, osArchitecture, javaVersion, javaVendor);

        extentReports.setSystemInfo("OS : ", osName);
        extentReports.setSystemInfo("OS Architecture : ", osArchitecture);
        extentReports.setSystemInfo("Java Version : ", javaVersion);
        extentReports.setSystemInfo("Java Vendor : ", javaVendor);
    }

    public static ExtentHtmlReporter createExtentHtmlReporter(String reportFileName) {
        extentHtmlReporter = new ExtentHtmlReporter(reportFileName);


        extentHtmlReporter.config().setDocumentTitle(REPORT_TITLE_DEFAULT);
        extentHtmlReporter.config().setReportName(REPORT_DISPLAY_NAME_DEFAULT);

        return extentHtmlReporter;
    }


    public static Map<Object, String> getSystemProperties() {
        Map<Object, String> map = new HashMap<>();
        try {
            Properties properties = System.getProperties();

            Set sysPropertiesKeys = properties.keySet();
            for (Object key : sysPropertiesKeys) {
                map.put(key, properties.getProperty((String) key));
            }

            // Ask in the community forum who needs and why this Ip information ?
            /*
            String ipAddress = InetAddress.getLocalHost().toString();
            map.put("machine.name", ipAddress.split("/")[0]);
            map.put("machine.address", ipAddress.split("/")[1]);
            */
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Extent reporting error. You can safely ignore this. But to fix this see:" + e);
        }

        return map;
    }

    public static void reportName(String reportName) {
        extentHtmlReporter.config().setReportName(reportName);
    }

    public static String getReportName() {
        return extentHtmlReporter.config().getReportName();
    }

}
