package org.jsmart.zerocode.openapi.types;

import java.util.Random;

/**
 * Generates a random number, with the possibility to set a seed.
 * The standard random generator of ZeroCode uses the ThreadLocalRandom
 * that does not allow to set a seed, making the test not repeatable
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
