package org.jsmart.online.hello;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class HelloFirstTest {

    @Test
    public void willAddTwoWholeNumbers() throws Exception {
        HelloFirst helloFirst = new HelloFirst();
        int sum = helloFirst.add(4, 6);

        assertThat("Did not add properly", sum, is(101));
    }
}