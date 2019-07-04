package org.jsmart.zerocode.core.utils;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 *  A fixed length random generator supports up to 19 digits
 * @author santhosh
 *
 */
public class FixedRandomGenerator {

	/*
	 * Generators are preserved to avoid creating duplicate instances 
	 * for the entire life cycle
	 * */
	private static final HashMap<Integer, FixedRandomGenerator> GENERATOR_MAP = new HashMap<>();

	private long lowerBound;
	private long upperBound;

	
	private FixedRandomGenerator(int length) {
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
	 * @param length of the random number(0 < length < 20)
	 * @return a {@link FixedRandomGenerator} number of given length
	 */
	public static FixedRandomGenerator getGenerator(int length) {
		FixedRandomGenerator buff = GENERATOR_MAP.get(length);
		if (buff == null) {
			buff = new FixedRandomGenerator(length);
			GENERATOR_MAP.put(length, buff);
			return buff;
		} else {
			return buff;
		}
	}
}
