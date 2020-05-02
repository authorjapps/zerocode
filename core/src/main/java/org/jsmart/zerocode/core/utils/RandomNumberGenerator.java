package org.jsmart.zerocode.core.utils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomNumberGenerator {

    public String generateRandomNumber() {
        return String.valueOf(Math.abs(ThreadLocalRandom.current().nextLong()));
    }

    @Override
    public String toString() {
        return this.generateRandomNumber();
    }
}
