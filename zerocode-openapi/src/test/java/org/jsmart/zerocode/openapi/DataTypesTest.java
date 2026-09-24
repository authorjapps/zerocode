package org.jsmart.zerocode.openapi;

import java.io.IOException;

import org.jsmart.zerocode.openapi.types.RandomGeneratorWithSeed;
import org.junit.Test;

public class DataTypesTest extends BaseTest {

	@Test
	public void testSchemaOtherDataTypesAndFormats() throws IOException {
		// int and string where tested with step scenarios, here testing number and dates
		new RandomGeneratorWithSeed().setSeed(9876543); // to allow repeatable tests for boolean
		ScenarioGenerator parser = new ScenarioGenerator();
		parser.generateAll(ymlFolder + "/oa-other-types.yml", outFolder);
		assertScenario("_testOtherTypes");
	}

	@Test
	public void testSchemaAdditionalProperties() throws IOException {
		ScenarioGenerator parser = new ScenarioGenerator();
		parser.generateAll(ymlFolder + "/oa-additional-properties.yml", outFolder);
		assertScenario("_testAdditionalProperties");
	}
	
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
