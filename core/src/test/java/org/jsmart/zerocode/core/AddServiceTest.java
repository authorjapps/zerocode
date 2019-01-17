package org.jsmart.zerocode.core;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AddServiceTest {

    @Test
    public void willAddTwoWholeNumbers() throws Exception {
        AddService addService = new AddService();
        int sum = addService.add(4, 6);

        assertThat("Addition failed for two whole numbers", sum, is(10));
    }
}