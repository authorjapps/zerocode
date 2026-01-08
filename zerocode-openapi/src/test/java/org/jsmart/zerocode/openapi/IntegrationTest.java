package org.jsmart.zerocode.openapi;

import java.io.IOException;

import org.jsmart.zerocode.openapi.types.RandomGeneratorWithSeed;
import org.junit.Before;
import org.junit.Test;

public class IntegrationTest extends BaseTest {

	@Before
	public void setUp() {
		ymlFolder = "src/test/resources/integration_test_files";
		outFolder = "target/integration_test_generated_files";
		bmkFolder = "src/test/resources/integration_test_generated_files";
	}

	// https://github.com/swagger-api/swagger-petstore
	// Swagger Petstore OpenAPI 3.0 release 1.0.19
	// https://github.com/swagger-api/swagger-petstore/archive/refs/tags/swagger-petstore-v3-1.0.19.zip
	@Test
	public void testSwaggerPetstoreIntegration() throws IOException {
		new RandomGeneratorWithSeed().setSeed(9876543); // to allow repeatable tests
		ScenarioGenerator parser = new ScenarioGenerator();
		parser.generateAll(ymlFolder + "/openapi.yaml", outFolder);
		assertAllScenarios("petstore_all.html");
	}

	// To check performance, use the GitHub Api specification: https://github.com/github/rest-api-description
	// https://raw.githubusercontent.com/github/rest-api-description/refs/heads/main/descriptions/ghes-3.14/ghes-3.14.json
	// Time to generate: between 7 and 11 seconds, 631 scenarios
}
