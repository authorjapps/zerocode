package org.jsmart.zerocode.openapi;

import java.io.IOException;

import org.jsmart.zerocode.openapi.types.RandomGeneratorWithSeed;
import org.junit.Test;

public class DataTypesTest extends BaseTest {

	@Test
	public void testSchemaEnumInQueryPathParamsAndBody() throws IOException {
		new RandomGeneratorWithSeed().setSeed(9876543); // to allow repeatable tests
		ScenarioGenerator parser = new ScenarioGenerator();
		parser.generateAll(ymlFolder + "/oa-enum.yml", outFolder);
		assertScenario("_testEnum_{pathEnum}");
	}

	@Test
	public void testSchemaEnumWithUrlEncode() throws IOException {
		new RandomGeneratorWithSeed().setSeed(9876543);
		ScenarioGenerator parser = new ScenarioGenerator();
		parser.generateAll(ymlFolder + "/oa-enum-urlencode.yml", outFolder);
		assertScenario("_testEnumUrlencode_{pathEnum}");
	}

	@Test
	public void testSchemaArraysInBody() throws IOException {
		ScenarioGenerator parser = new ScenarioGenerator();
		parser.generateAll(ymlFolder + "/oa-array.yml", outFolder);
		assertScenario("_testArray");
	}

}
