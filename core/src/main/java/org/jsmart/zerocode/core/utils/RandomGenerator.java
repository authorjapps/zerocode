package org.jsmart.zerocode.core.utils;

/**
 * 
 * A random generator which gives current milli second
 * 
 * @author santhosh
 *
 */
public class RandomGenerator {

	// Only once instance for entire life cycle
	private static RandomGenerator generator = new RandomGenerator();

	private RandomGenerator() {
		// private constructor to prevent external object creation
	}
	/**
	 * @return current milli second
	 */
	@Override
	public String toString() {
		return String.valueOf(System.currentTimeMillis());
	}

	/**
	 * @return a singleton instance of {@link RandomGenerator} 
	 */
	public static RandomGenerator getGenerator() {
		return generator;

	}
}
