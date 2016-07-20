package org.jsmart.zerocode.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddService {
    private static final Logger logger = LoggerFactory.getLogger(AddService.class);

    public int add(int i, int i1) {
        logger.info("i= " + i + ", j= " + i1);
        return i + i1;
    }

    public Integer square(Integer number){

        return number * number;
    }

    public Integer squareMyNumber(MyNumber myNumber){
        logger.info("Calculating Square of " + myNumber.getNumber());
        return myNumber.getNumber() * myNumber.getNumber();
    }

}

class MyNumber{
    Integer number;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}