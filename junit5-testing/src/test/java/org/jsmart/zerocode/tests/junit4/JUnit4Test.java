package org.jsmart.zerocode.tests.junit4;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class JUnit4Test {

  @Test
  public void testJunit4_passed() {
    String msg = "junit4 works as usual";
    assertTrue("junit4 works as usual".equals(msg));
  }
}
