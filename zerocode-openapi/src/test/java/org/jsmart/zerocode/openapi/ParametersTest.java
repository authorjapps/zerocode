package org.jsmart.zerocode.openapi;

import java.io.IOException;

import org.jsmart.zerocode.openapi.types.RandomGeneratorWithSeed;
import org.junit.Test;

public class ParametersTest extends BaseTest {

	@Test
	public void testQueryParamsWithFormStyleAndUrlEncoding() throws IOException {
		new RandomGeneratorWithSeed().setSeed(98765432); // to allow repeatable tests for enum
		ScenarioGenerator parser = new ScenarioGenerator();
		parser.generateAll(ymlFolder + "/oa-query-param-form.yml", outFolder);
		assertScenario("_testQueryParamForm");
	}

	@Test
	public void testQueryParamsWithOtherStyleAndUnsupported() throws IOException {
		ScenarioGenerator parser = new ScenarioGenerator();
		parser.generateAll(ymlFolder + "/oa-query-param-other.yml", outFolder);
		assertScenario("_testQueryParamOther");
	}

	@Test
	public void testPathParamsWithUrlEncodingAndUnsupported() throws IOException {
		new RandomGeneratorWithSeed().setSeed(98765432); // to allow repeatable tests for enum
		ScenarioGenerator parser = new ScenarioGenerator();
		parser.generateAll(ymlFolder + "/oa-path-param.yml", outFolder);
		assertScenario("_testPathParam_{par Enum}_array_{parArray}_object{parObject}");
	}

	@Test
	public void testHeaderParams() throws IOException {
		ScenarioGenerator parser = new ScenarioGenerator();
		parser.generateAll(ymlFolder + "/oa-header-param.yml", outFolder);
		assertScenario("_testHeaderParam");
	}

	@Test
	public void testHeaderFromMediatype() throws IOException {
		ScenarioGenerator parser = new ScenarioGenerator();
		parser.generateAll(ymlFolder + "/oa-header-mediatype.yml", outFolder);
		assertScenario("_testHeaderMediatype");
	}

	@Test
	public void testHeaderParamsAndMediatype() throws IOException {
		ScenarioGenerator parser = new ScenarioGenerator();
		parser.generateAll(ymlFolder + "/oa-header-param-mediatype.yml", outFolder);
		assertScenario("_testHeaderParamMediatype");
	}
}
