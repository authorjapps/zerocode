package org.jsmart.zerocode.jupiter.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.apiguardian.api.API;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.domain.LoadWith;
import org.jsmart.zerocode.core.domain.TestMapping;
import org.jsmart.zerocode.core.report.ZeroCodeReportGenerator;
import org.jsmart.zerocode.core.report.ZeroCodeReportGeneratorImpl;
import org.jsmart.zerocode.jupiter.load.JupiterLoadProcessor;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * This JUnit5 extension helps to generate load declaratively for performance testing. We can
 * simply cherry-pick existing/new JUnit5 tests and run them in parallel as configured by the
 * load-properties.
 * <p>
 * This class implements BeforeEachCallback(not BeforeAllCallback), because the load generation
 * can be done for each test method. This way we don't need to create multiple load generation
 * classes and add them into a Test-Suite runner. Instead, just create multiple test-methods with
 * annotating with @Test(from Jupiter package) and @TestMappings(...).
 * <p>
 * Visit the Wiki page and HelloWorld repo on this for precise examples.
 */
@API(status = EXPERIMENTAL)
public class ParallelLoadExtension implements BeforeEachCallback {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParallelLoadExtension.class);
    private final ObjectMapper mapper = new ObjectMapperProvider().get();
    private final ZeroCodeReportGenerator reportGenerator = new ZeroCodeReportGeneratorImpl(mapper);

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        Method testMethod = extensionContext.getRequiredTestMethod();
        Class<?> testClass = extensionContext.getRequiredTestClass();
        String loadPropertiesFile = validateAndGetLoadPropertiesFile(testClass, testMethod);
        JupiterLoadProcessor loadProcessor = new JupiterLoadProcessor(loadPropertiesFile);

        //-------------------------------------------
        //       On/Off Extent-Report switch
        //-------------------------------------------
        // Load the key 'chart.dashboard.generation'
        // from 'loadPropertiesFile'
        //-------------------------------------------
        boolean chartAndDashBoardGenerationEnabled = false;


        TestMapping[] testMappingArray = testMethod.getAnnotationsByType(TestMapping.class);

        Arrays.stream(testMappingArray).forEach(thisMapping -> {
            loadProcessor.addJupiterTest(thisMapping.testClass(), thisMapping.testMethod());
        });

        boolean hasFailed = loadProcessor.processMultiLoad();

        reportGenerator.generateCsvReport();
        if (chartAndDashBoardGenerationEnabled) {
            reportGenerator.generateExtentReport();
        }

        if (hasFailed) {
            failTest(testMethod, testClass);
        } else {
            LOGGER.debug("\nAll Passed \uD83D\uDC3C. \nSee the granular 'csv report' for individual test statistics.");
        }

    }

    private void failTest(Method testMethod, Class<?> testClass) {
        String failureMessage = testClass.getName() + " with load/stress test(s): " + testMethod + " have Failed";
        LOGGER.error("\n" + failureMessage + ". \n\uD83D\uDC47" +
                "\na) See the 'target/' for granular 'csv report' for pass/fail/response-delay statistics.\uD83D\uDE0E" +
                "\n-Also- " +
                "\nb) See the 'target/logs' for individual failures by their correlation-ID.\n\n");
        String testDescription = testClass + "#" + testMethod;

        fail(testDescription, new RuntimeException(failureMessage));
    }

    protected String validateAndGetLoadPropertiesFile(Class<?> testClass, Method method) {
        LoadWith loadClassWith = testClass.getAnnotation(LoadWith.class);
        LoadWith loadMethodWith = method.getAnnotation(LoadWith.class);

        if (loadClassWith != null) {
            return loadClassWith.value();
        }

        if (loadMethodWith != null) {
            return loadMethodWith.value();
        }

        throw new RuntimeException(
                format("\n<< Ah! Missing the the @LoadWith(...) on this Class '%s' or Method '%s' >> ",
                        testClass.getName(), method.getName())
        );

    }

}
