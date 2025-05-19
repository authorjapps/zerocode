package org.jsmart.zerocode.core.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A JUnit 4 {@link TestRule} that integrates Guice dependency injection into test classes.
 * This rule creates a Guice {@link Injector} using the specified module and injects dependencies
 * into the test instance before executing each test method.
 */
public class ZeroCodeGuiceTestRule implements TestRule {
    private final Object testInstance;
    private final Class<? extends com.google.inject.Module> moduleClass;

    public ZeroCodeGuiceTestRule(Object testInstance, Class<? extends com.google.inject.Module> moduleClass) {
        this.testInstance = testInstance;
        this.moduleClass = moduleClass;
    }
    /**
     * Applies Guice dependency injection to the test instance before executing the test.
     * Creates a Guice {@link Injector} using the specified module, injects dependencies into
     * the test instance, and then evaluates the base test statement.
     **/
    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Injector injector = Guice.createInjector(moduleClass.getDeclaredConstructor().newInstance());
                injector.injectMembers(testInstance);
                base.evaluate();
            }
        };
    }
}