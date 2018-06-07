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

import java.util.Arrays;
import java.util.List;

import static org.junit.runner.Description.createTestDescription;

public class ZeroCodeLoadRunner extends ParentRunner<TestMapping> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZeroCodeLoadRunner.class);
    public static final String LOAD_LABEL = "<<Load>>";

    private final Class<?> testClass;
    private LoadProcessor loadProcessor;
    private String loadPropertiesFile;
    private Description testDescription;

    public ZeroCodeLoadRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        this.testClass = testClass;
        this.loadPropertiesFile = validateAndGetLoadPropertiesFile();
        loadProcessor = new LoadProcessor(loadPropertiesFile);
    }

    @Override
    protected List<TestMapping> getChildren() {
        validateAnnotationPresence();
        validateTestMethod();
        return Arrays.asList(testClass.getAnnotationsByType(TestMapping.class));
    }

    @Override
    protected Description describeChild(TestMapping child) {
        this.testDescription = createTestDescription(testClass, LOAD_LABEL + child.testMethod());
        return testDescription;
    }

    @Override
    protected void runChild(TestMapping child, RunNotifier notifier) {
        notifier.fireTestStarted(testDescription);

        boolean hasFailed = loadProcessor
                .addTest(child.testClass(), child.testMethod())
                .process();

        if(hasFailed){
            String failureMessage = testClass.getName() + "." + child.testMethod() + " Failed";
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
        if(loadWithAnno == null){
            throw new RuntimeException("Ah! You missed to put the @LoadWith(...) on the load-generating test class >> "
                    + testClass.getName());
        }

        return loadWithAnno.value();
    }

    private void validateAnnotationPresence() {
        TestMapping methodMapping = testClass.getAnnotation(TestMapping.class);
        TestMapping[] testMappings = testClass.getAnnotationsByType(TestMapping.class);

        if (testMappings.length > 1){
            throw new RuntimeException("Oops! Needs single @TestMapping, but found multiple of it on the load-generating test class >>"
                    + testClass.getName() + ". \n For running multiple tests as load use @RunWith(ZeroCodeMultiLoadRunner.class)");

        } else if (methodMapping == null) {
            throw new RuntimeException("Ah! You missed to put the @TestMapping on the load-generating test class >> "
                    + testClass.getName());

        }

    }

    private void validateTestMethod() {
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

}
