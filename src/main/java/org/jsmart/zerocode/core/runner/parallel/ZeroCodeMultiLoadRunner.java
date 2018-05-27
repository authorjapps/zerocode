package org.jsmart.zerocode.core.runner.parallel;

import org.jsmart.zerocode.core.domain.TestMapping;
import org.jsmart.zerocode.core.domain.TestMappings;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

// TODO-- wip...
public class ZeroCodeMultiLoadRunner extends ParentRunner<TestMapping> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZeroCodeMultiLoadRunner.class);

    private final Class<?> testClass;

    public ZeroCodeMultiLoadRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        this.testClass = testClass;
    }

    @Override
    protected List<TestMapping> getChildren() {
        validateAnnotationPresence();
        return Arrays.asList(testClass.getAnnotationsByType(TestMapping.class));
    }

    @Override
    protected Description describeChild(TestMapping child) {
        return Description.createTestDescription(testClass, child.testMethod());
    }

    @Override
    protected void runChild(TestMapping child, RunNotifier notifier) {
        final Description description = Description.createTestDescription(testClass, child.testMethod());
        notifier.fireTestStarted(description);

        Result result = (new JUnitCore()).run(Request.method(child.testClass(), child.testMethod()));

        if(!result.wasSuccessful()){
            String failureMessage = testClass.getName() + "." + child.testMethod() + " Failed";
            LOGGER.error(failureMessage + ". See target/logs -or- junit granular failure report(csv) -or- fuzzy search and filter report(html) for details");
            notifier.fireTestFailure(new Failure(description, new RuntimeException(failureMessage)));
        }
        notifier.fireTestFinished(description);
    }

    @Override
    public void run(RunNotifier notifier) {
        super.run(notifier);
    }

    private void validateAnnotationPresence() {
        TestMappings testMappings = testClass.getAnnotation(TestMappings.class);
        TestMapping[] methodMappings = testClass.getAnnotationsByType(TestMapping.class);
        if (testMappings == null || methodMappings == null) {
            throw new RuntimeException("Ah! You missed to put the @TestMappings or @TestMapping");
        }
    }

}
