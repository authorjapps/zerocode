package org.jsmart.zerocode.core.junit;

import org.jsmart.zerocode.core.domain.HostProperties;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.*;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@HostProperties(host="http://localhost", port=9998, context = "")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class JunitUsualWithZerocodeUnitTest {

    @BeforeClass
    public static void setUpBeforeClass() {
        System.out.println("I am at BeforeClass.");
    }

    @AfterClass
    public static void setUpAfterClass() {
        System.out.println("I am at AfterClass.");
    }

    @Before
    public void setUp() {
        System.out.println("I am at Before.");
    }

    @After
    public void tearDown() {
        System.out.println("I am at After.");
    }

    @Test
    public void test1Is1() {
        System.out.println("Checking 1 == 1");
        assertTrue(1 == 1);
    }

    @Test
    public void test2Is2() {
        System.out.println("Checking 2 == 2");
        assertTrue(2 == 2);
    }

    @Ignore("Pure JUnit failure - Un-Ignore this to run manually to view the failure report in CSV format")
    @Test
    public void test2Is3() {
        System.out.println("Checking 2 == 3");
        assertTrue(2 == 3);
    }

    /**
     * Mock end points are in test/resources: simulators/test_purpose_end_points.json.
     * @RunWith(TestOnlyZeroCodeUnitRunner.class) : starts these mocks first before running the tests
     */
    @Test
    @JsonTestCase("get_api/simple_get_api_test.json")
    public void testSimpleGetApi() throws Exception {
    
    }

}



