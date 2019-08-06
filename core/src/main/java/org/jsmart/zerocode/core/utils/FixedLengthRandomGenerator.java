package org.jsmart.zerocode.core.utils;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A fixed length random generator supports up to 19 digits
 */
public class FixedLengthRandomGenerator {

    /*
     * Generators are preserved to avoid creating duplicate instances
     * for the entire life cycle
     * */
    private static final HashMap<Integer, FixedLengthRandomGenerator> GENERATOR_MAP = new HashMap<>();

    private long lowerBound;
    private long upperBound;


    private FixedLengthRandomGenerator(int length) {
        this.lowerBound = (long) Math.pow(10, (length - 1));
        this.upperBound = (long) Math.pow(10, length);
    }

    public String generateRandomNumber() {
        return String.valueOf(ThreadLocalRandom.current().nextLong(this.lowerBound, this.upperBound));
    }

    @Override
    public String toString() {
        return this.generateRandomNumber();
    }

    /**
     * @param length of the random number(1 to 19 i.e. between 0 to 20)
     * @return a {@link FixedLengthRandomGenerator} number of given length
     */
    public static FixedLengthRandomGenerator getGenerator(int length) {
        if (length < 1 || length > 19) {
            throw new RuntimeException("length of the random number should be between (1-19)");
        }
        FixedLengthRandomGenerator buff = GENERATOR_MAP.get(length);
        if (buff == null) {
            buff = new FixedLengthRandomGenerator(length);
            GENERATOR_MAP.put(length, buff);
            return buff;
        } else {
            return buff;
        }
    }
}
