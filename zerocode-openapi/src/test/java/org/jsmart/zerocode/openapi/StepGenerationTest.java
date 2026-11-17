package org.jsmart.zerocode.openapi;

import java.io.IOException;

import org.junit.Test;

public class StepGenerationTest extends BaseTest {

	@Test
	public void testFull_WithQueryPathParamsAndBody() throws IOException {
		ScenarioGenerator parser = new ScenarioGenerator();
		parser.generateAll(ymlFolder + "/oa-full.yml", outFolder);
		assertScenario("_testFull_{pparam1}_more_{pparam2}");
	}

	@Test
	public void testEmpty_WithOperationOnly() throws IOException {
		ScenarioGenerator parser = new ScenarioGenerator();
		parser.generateAll(ymlFolder + "/oa-empty.yml", outFolder);
		assertScenario("_testEmpty");
	}
	
	@Test
	public void testFailedStep_DoesNotBlockGeneration() throws IOException {
		ScenarioGenerator parser = new ScenarioGenerator();
		parser.generateAll(ymlFolder + "/oa-failed-step.yml", outFolder);
		assertScenario("_testFailedStep");
	}

	@Test
	public void testStringBody() throws IOException {
		ScenarioGenerator parser = new ScenarioGenerator();
		parser.generateAll(ymlFolder + "/oa-string-body.yml", outFolder);
		assertScenario("_testStringBody");
	}
	
	@Test
	public void testArrayBody() throws IOException {
		ScenarioGenerator parser = new ScenarioGenerator();
		parser.generateAll(ymlFolder + "/oa-array-body.yml", outFolder);
		assertScenario("_testArrayBody");
	}
	
}
