package org.jsmart.zerocode.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddService {
    private static final Logger logger = LoggerFactory.getLogger(AddService.class);

    public int add(int i, int i1) {
        logger.debug("i= " + i + ", j= " + i1);
        return i + i1;
    }

    public Integer square(Integer number) {

        return number * number;
    }

    public Integer squareMyNumber(MyNumber myNumber) {
        logger.debug("Calculating Square of " + myNumber.getNumber());
        return myNumber.getNumber() * myNumber.getNumber();
    }

    public Double squareRoot(Double number) {
    	if (number < 0.0)
    		throw new RuntimeException("Can not square root a negative number");
        return Math.sqrt(number);
    }
    
    public Integer anInteger() {
        logger.debug("Returning a number ");

        return 30;
    }

}

class MyNumber {
    Integer number;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}