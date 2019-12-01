package org.jsmart.zerocode.core.engine.assertion;

import java.math.BigDecimal;
import java.util.Comparator;

public class NumberComparator implements Comparator<Number> {

    /**
     * Compares two numbers and returns their differences.
     * e.g. 3 compare 3.0 = 0, 3.0 compare 3 = 0, 3 compare 3.12 = -1, 3.12 compare 3 = 1
     *
     * @param a One of two numbers to compare
     * @param b Two of two numbers to compare
     * @return a-b
     */
    public int compare(Number a, Number b){
        return new BigDecimal(a.toString()).compareTo(new BigDecimal(b.toString()));
    }

}
