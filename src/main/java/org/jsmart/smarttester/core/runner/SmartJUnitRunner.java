package org.jsmart.smarttester.core.runner;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.util.ArrayList;
import java.util.List;

public class SmartJUnitRunner extends BlockJUnit4ClassRunner {
    /**
     * Creates a BlockJUnit4ClassRunner to run {@code klass}
     *
     * @param klass
     * @throws InitializationError if the test class is malformed.
     */
    List<String> smartTestCaseNames = new ArrayList<>();
    String currentTestCase;

    public SmartJUnitRunner(Class<?> klass) throws InitializationError {
        super(klass);
        smartTestCaseNames = getSmartChildrenList();
    }

    private List<String> getSmartChildrenList() {
        List<FrameworkMethod> children = getChildren();
        children.forEach(
                frameworkMethod -> {
                    SmartTestCase annotation = frameworkMethod.getAnnotation(SmartTestCase.class);
                    if (annotation != null) {
                        smartTestCaseNames.add(annotation.value());
                    } else {
                        smartTestCaseNames.add(frameworkMethod.getName());
                    }
                }
        );

        return smartTestCaseNames;
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {

        SmartTestCase annotation = method.getMethod().getAnnotation(SmartTestCase.class);

        if (annotation != null) {
            currentTestCase = annotation.value();
        } else {
            currentTestCase = method.getName();
        }

        /**
         * TODO: Run the Single JSON and assert, create the JUNIT Assertion report.
         */
        // TODO

        super.runChild(method, notifier);
    }

    public List<String> getSmartTestCaseNames() {
        return smartTestCaseNames;
    }

    public String getCurrentTestCase() {
        return currentTestCase;
    }
}
