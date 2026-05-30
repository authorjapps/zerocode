package org.jsmart.zerocode.core.runner;

import com.google.inject.Injector;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodePackageRunner;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit tests for ZeroCodePackageRunner system property handling.
 * For both Test Suite folder and host env properties.
 */
public class ZeroCodePackageRunnerSystemPropertyTest {

    // Dummy test class used when instantiating the runner
    public static class DummyTest {
    }

    // Dummy test class annotated with @TargetEnv to test fallback behavior
    @TargetEnv("config_hosts.properties")
    public static class AnnotatedDummyTest {
    }

    @After
    public void tearDown() {
        // Clear the property so other tests are not affected
        System.clearProperty("zerocode.env");
        System.clearProperty("zerocode.folder");
    }

    @Test
    public void shouldUseSystemPropertyWhenPresent() throws InitializationError {
        // Ensure property is set
        System.setProperty("zerocode.env", "config_hosts.properties");

        // Create injector using a test class annotated with @TargetEnv
        TestOnlyZeroCodePackageRunner runner = new TestOnlyZeroCodePackageRunner(DummyTest.class);
        Injector injector = runner.getMainModuleInjector();

        // Assert
        assertNotNull("Injector should not be null when zerocode.env is set", injector);
        assertNotNull("SmartUtils should be bound in injector", injector.getInstance(SmartUtils.class));
    }

    @Test
    public void shouldFallbackToTargetEnvWhenSystemProperty_isNotSet() throws InitializationError {
        // Ensure property is not set
        System.clearProperty("zerocode.env");

        // Create injector using a test class annotated with @TargetEnv
        TestOnlyZeroCodePackageRunner runner = new TestOnlyZeroCodePackageRunner(AnnotatedDummyTest.class);
        Injector injector = runner.getMainModuleInjector();

        // Assert
        assertNotNull("Injector should be created even when zerocode.env is absent (fallback)", injector);
        assertNotNull("SmartUtils should be bound in injector on fallback", injector.getInstance(SmartUtils.class));
    }

    @Test
    public void shouldPreferSystemPropertyOverTargetEnv_IfProvidedFileExists() throws Exception {
        // Arrange - set system property to a test-specific properties file that should exist in test resources
        System.setProperty("zerocode.env", "config_hosts_test_ci.properties");

        // Create injector
        TestOnlyZeroCodePackageRunner runner = new TestOnlyZeroCodePackageRunner(AnnotatedDummyTest.class);
        Injector injector = runner.getMainModuleInjector();

        // Assert basic injector creation
        assertNotNull("Injector should not be null when zerocode.env overrides TargetEnv", injector);

        // Also verify the overridden properties file exists on the test classpath (sanity check for the test resource)
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("config_hosts_test_ci.properties");
        assertNotNull("config_hosts_test_ci.properties must be present in test resources for this test", in);

        // Optionally verify a known property in the test resource (if that resource contains the property)
        Properties props = new Properties();
        props.load(in);
        // If your test resource has a different host value, update the expected value accordingly.
        assertEquals("http://localhost-test", props.getProperty("web.application.endpoint.host"));
    }

    @Test
    public void shouldReadZerocodeFolderSystemProperty_sanityTest() {
        System.setProperty("zerocode.folder", "any_test_folder");
        String folderOverride = System.getProperty("zerocode.folder");
        assertNotNull("zerocode.folder should be readable via System.getProperty", folderOverride);
        assertEquals("any_test_folder", folderOverride);
    }

    @Test
    public void shouldUseZerocodeFolderSystemPropertyToListFiles() {
        // Point to test resources (relative path from project root). Adjust if your project layout differs.
        System.setProperty("zerocode.folder", "load_test_files");

        String folderOverride = System.getProperty("zerocode.folder");
        assertNotNull("zerocode.folder must be set for this test", folderOverride);

        // Use SmartUtils to list files under the provided folder path.
        List<String> files = SmartUtils.getAllEndPointFiles(folderOverride);

        assertNotNull("SmartUtils.getAllEndPointFiles should return a non-null list", files);
        assertFalse("SmartUtils.getAllEndPointFiles should find at least one file in the folderOverride path", files.isEmpty());
    }

    @Test
    public void shouldThrowMeaningfulExceptionMsg_ifFolderDoesNotExist() {
        // setup stuff: set a folder path that does not exist
        final String missingFolder = "load_test_filesX";
        System.setProperty("zerocode.folder", missingFolder);

        try {
            // call method that should throw
            SmartUtils.getAllEndPointFiles(System.getProperty("zerocode.folder"));

            // It will never reach here. If reached, fail the test.
            fail("Expected RuntimeException due to missing folder '" + missingFolder + "'");
        } catch (RuntimeException e) {
            // Assert: message contains expected diagnostic text and folder name
            final String msg = e.getMessage();
            assertNotNull("Exception message should be present", msg);
            assertTrue("Exception text has folder related message",
                    msg.contains("Check the (load_test_filesX) integration test repo folder(empty?). "));
            assertTrue("Exception message should contain NothingFoundHereException indicator",
                    msg.contains("NothingFoundHereException"));

        }
    }

}