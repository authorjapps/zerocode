package org.jsmart.zerocode.tests.loadjupiter;

import org.jsmart.zerocode.core.domain.LoadWith;
import org.jsmart.zerocode.core.domain.TestMapping;
import org.jsmart.zerocode.core.domain.TestMappings;
import org.jsmart.zerocode.jupiter.extension.ParallelLoadExtension;
import org.jsmart.zerocode.tests.jupiter.JUnit5Test;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({ParallelLoadExtension.class})
public class JUnit5LoadTest {

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