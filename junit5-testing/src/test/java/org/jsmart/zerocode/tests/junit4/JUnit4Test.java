package org.jsmart.zerocode.tests.junit4;


import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class JUnit4Test {

    @Test
    public void testJunit4_passed() {
        String msg = "junit4 works as usual";
        assertTrue("junit4 works as usual".equals(msg));
    }

}