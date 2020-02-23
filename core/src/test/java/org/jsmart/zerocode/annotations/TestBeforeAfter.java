package org.jsmart.zerocode.annotations;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(ZeroCodeUnitRunner.class)
public class TestBeforeAfter {

    private static int variable = 0;

    @Before
    public void setUp() {
        assertEquals(0, variable);
        variable = 1;
    }

    @After
    public void tearDown() {
        assertEquals(1, variable);
        variable = 0;
    }

    public void assertBeforeIsCalled(String request) {
        assertEquals(1, variable);
    }

    @Test
    @JsonTestCase("integration_test_files/annotations/assert_before.json")
    public void test_before_is_called_1() throws Exception {

    }

    @Test
    @JsonTestCase("integration_test_files/annotations/assert_before.json")
    public void test_before_is_called_2() throws Exception {

    }
}
