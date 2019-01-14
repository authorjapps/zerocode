package org.jsmart.zerocode.core.engine.assertion;

import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class NumberComparatorTest {

    @Test
    public void willCompareTwoDifferentNumbers_Rightly() throws Exception {
        assertThat((new BigDecimal("3")).compareTo(new BigDecimal("3")), is(0));
        assertThat((new BigDecimal("3.0")).compareTo(new BigDecimal("3")), is(0));
        assertThat((new BigDecimal("3")).compareTo(new BigDecimal("3.0009")), is(-1));
        assertThat((new BigDecimal("3.0009")).compareTo(new BigDecimal("3")), is(1));
        assertThat((new BigDecimal("3.00009")).compareTo(new BigDecimal("3.00009")), is(0));

        NumberComparator comparator = new NumberComparator();
        assertThat(comparator.compare(new Integer("3"), new Long("3")), is(0));
        assertThat(comparator.compare(new Integer("3"), new Double("3.0")), is(0));

    }

}