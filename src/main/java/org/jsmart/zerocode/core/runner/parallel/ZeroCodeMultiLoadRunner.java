package org.jsmart.zerocode.core.runner.parallel;

import org.jsmart.zerocode.core.domain.LoadWith;
import org.jsmart.zerocode.core.domain.TestMapping;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.runner.Description.createTestDescription;

public class ZeroCodeMultiLoadRunner extends ParentRunner<TestMapping[]> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZeroCodeMultiLoadRunner.class);
    public static final String LOAD_LABEL = "<<Multi-Load>>";

    private final Class<?> testClass;
    private LoadProcessor loadProcessor;
    private String loadPropertiesFile;
    private Description testDescription;

    public ZeroCodeMultiLoadRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        this.testClass = testClass;
        this.loadPropertiesFile = validateAndGetLoadPropertiesFile();
        loadProcessor = new LoadProcessor(loadPropertiesFile);
    }

    @Override
    protected List<TestMapping[]> getChildren() {
        validateAnnotationPresence();
        validateTestMethod();
        return getTestMappingsArrayAsSingleElementList();
    }

    private List<TestMapping[]> getTestMappingsArrayAsSingleElementList() {
        TestMapping[] testMappings = testClass.getAnnotationsByType(TestMapping.class);
        List<TestMapping[]> testMappingsSingleElementList = new ArrayList<>();
        testMappingsSingleElementList.add(testMappings);
        return testMappingsSingleElementList;
    }

    @Override
    protected Description describeChild(TestMapping[] childArrayElement) {
        String multiLoadLabel = createMultiLoadLabel(childArrayElement);
        System.out.println("### label = " + multiLoadLabel + "\n");
        this.testDescription = createTestDescription(testClass, LOAD_LABEL + multiLoadLabel);
        return testDescription;
    }

    @Override
    protected void runChild(TestMapping[] childArrayElement, RunNotifier notifier) {
        notifier.fireTestStarted(testDescription);

        Arrays.stream(childArrayElement).forEach(thisChild -> {
            loadProcessor.addTest(thisChild.testClass(), thisChild.testMethod());
        });

        boolean hasFailed = loadProcessor.processMultiLoad();

        if (hasFailed) {
            String failureMessage = testClass.getName() + " with load/stress test(s): " + testDescription + " have Failed";
            LOGGER.error(failureMessage + ". See target/logs -or- junit granular failure report(csv) -or- fuzzy search and filter report(html) for details");
            notifier.fireTestFailure(new Failure(testDescription, new RuntimeException(failureMessage)));
        }
        notifier.fireTestFinished(testDescription);
    }

    @Override
    public void run(RunNotifier notifier) {
        super.run(notifier);
    }

    private String validateAndGetLoadPropertiesFile() {
        LoadWith loadWithAnno = testClass.getAnnotation(LoadWith.class);
        if (loadWithAnno == null) {
            throw new RuntimeException("Ah! You missed to put the @LoadWith(...) on the load-generating test class >> "
                    + testClass.getName());
        }

        return loadWithAnno.value();
    }

    private void validateAnnotationPresence() {
        TestMapping[] testMappings = testClass.getAnnotationsByType(TestMapping.class);

        if (testMappings.length == 0) {
            throw new RuntimeException("Ah! You missed to put the @TestMapping on the load-generating test class >>"
                    + testClass.getName() + ".");

        }
    }

    private void validateTestMethod() {
        TestMapping[] testMappings = testClass.getAnnotationsByType(TestMapping.class);
        Arrays.stream(testMappings).forEach(methodMapping -> {
            String errMessage = " was invalid, please re-check and pick the correct test method to load.";
            try {
                errMessage = "Mapped test method `" + methodMapping.testMethod() + "`" + errMessage;
                methodMapping.testClass().getMethod(methodMapping.testMethod());
            } catch (NoSuchMethodException e) {
                LOGGER.error(errMessage);
                throw new RuntimeException(errMessage + e);
            }
        });
    }

    private String createMultiLoadLabel(TestMapping[] childArrayElement) {
        AtomicInteger counter = new AtomicInteger(0);
        return Arrays.stream(childArrayElement)
                .map(thisChild ->  "\n" + counter.incrementAndGet() + ")"
                        + thisChild.testClass().getSimpleName()
                        + "." + thisChild.testMethod())
                .collect(Collectors.joining(",")) + "\n";
    }
}
