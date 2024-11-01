package org.jsmart.zerocode.openapi.types;

import java.util.Random;

/**
 * Generates a random number, with the possibility to set a seed.
 * The standard random generator of ZeroCode uses the ThreadLocalRandom
 * that does not allow to set a seed, making the test not repeatable
 * 
 * TODO core enhancement Add a token to generate a random value among a set of values
 * It should allow generate both strings and non strings (quoted and unquoted values)
 * Motivation: The ONE.OF token can be used only to check values in the asserts, 
 * not to generate values in the body. If this feature is included, could
 * be used to generate enum and boolean values
 */
public class RandomGeneratorWithSeed {

	private static Random random;

	public RandomGeneratorWithSeed() {
		if (random == null)
			random = new Random();
	}

	public void setSeed(long seed) {
		random.setSeed(seed);
	}

	public int nextInt(int origin, int bound) {
		return Math.abs(random.nextInt()) % (bound - origin + 1);
	}
}
