package org.jsmart.zerocode.jupiter.extension;

import java.lang.reflect.Method;
import org.jsmart.zerocode.core.domain.LoadWith;
import org.jsmart.zerocode.core.domain.TestMapping;
import org.jsmart.zerocode.core.domain.TestMappings;
import org.jsmart.zerocode.tests.jupiter.JUnit5Test;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParallelLoadExtensionTest {

    @ExtendWith({ParallelLoadExtension.class})
    public class JUnit5ExampleLoad {

        @Test
        //@LoadWith("load_generation.properties") //missing case
        @TestMappings({
                @TestMapping(testClass = JUnit5Test.class, testMethod = "testX"),
                @TestMapping(testClass = JUnit5Test.class, testMethod = "testY")
        })
        public void testLoad() {
            /* No code needed here */
        }
    }

    @Test
    void testValidation_exception() {
        Class<JUnit5ExampleLoad> clazz = JUnit5ExampleLoad.class;
        Method[] declaredMethods = clazz.getDeclaredMethods();
        Method testMethod = declaredMethods[0]; // this is 'testLoad()'

        ParallelLoadExtension parallelLoadExtension = new ParallelLoadExtension();
        RuntimeException thrown =
                assertThrows(RuntimeException.class,
                        () -> parallelLoadExtension.validateAndGetLoadPropertiesFile(clazz, testMethod),
                        "Expected to throw RuntimeException, but it didn't throw.");

        assertTrue(thrown.getMessage().contains("Missing the the @LoadWith(...)"));
    }

    @ExtendWith({ParallelLoadExtension.class})
    public class JUnit5ExampleLoad2 {

        @Test
        @LoadWith("load_generation.properties")
        @TestMappings({
                @TestMapping(testClass = JUnit5Test.class, testMethod = "testX"),
                @TestMapping(testClass = JUnit5Test.class, testMethod = "testY")
        })
        public void testLoad() {
            /* No code needed here */
        }
    }

    @Test
    void testLoadAnnotation_success() {
        Class<JUnit5ExampleLoad2> clazz = JUnit5ExampleLoad2.class;
        Method[] declaredMethods = clazz.getDeclaredMethods();
        Method testMethod = declaredMethods[0]; // this is 'testLoad()'

        ParallelLoadExtension parallelLoadExtension = new ParallelLoadExtension();
        String loadPropertiesFile = parallelLoadExtension.validateAndGetLoadPropertiesFile(clazz, testMethod);

        assertEquals("load_generation.properties", loadPropertiesFile);
    }
}