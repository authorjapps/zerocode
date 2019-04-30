package com.zerocode.oas.stepgen.io;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import io.swagger.v3.oas.models.OpenAPI;


/**
 * The Class IOUtilsTest.
 */
@RunWith(SpringRunner.class)
public class IOUtilsTest {
	
	/**
	 * The Class IOUtilsTestTestContextConfiguration.
	 */
	@TestConfiguration
	static class IOUtilsTestTestContextConfiguration {
		
		/**
		 * Employee service.
		 *
		 * @return the IO utils
		 */
		@Bean
		public IOUtils employeeService() {
			return new IOUtils();
		}
	}

	/** The test content. */
	Map testContent;
	
	/** The output file location. */
	String outputFileLocation = "test.json";
	
	/** The input json contract location. */
	String inputJsonContractLocation = "sample.json";
	
	/** The input yaml contract location. */
	String inputYamlContractLocation = "petStore.yaml";
	
	/** The input contract URL. */
	String inputContractURL = "https://petstore.swagger.io/v2/swagger.json";
	
	/** The io utils. */
	@Autowired
	IOUtils ioUtils;

	/**
	 * Setup.
	 *
	 * @throws JSONException the JSON exception
	 */
	@Before
	public void setup() throws JSONException {
		testContent = new HashMap();
		testContent.put("id", 1);
		testContent.put("projectName", "zerocode");
		testContent.put("moduleName", "stepgen");
	}

	/**
	 * Test output with overwrite one time.
	 */
	@Test
	public void testOutputWithOverwriteOneTime() {
		deleteIfExists();
		ioUtils.ouputStepsToFile(testContent, outputFileLocation, false);
		assertTrue(ioUtils.fileExists(outputFileLocation));
	}

	/**
	 * Delete if exists.
	 */
	private void deleteIfExists() {
		if (ioUtils.fileExists(outputFileLocation)) {
			ioUtils.deleteFile(outputFileLocation);
		}
	}

	/**
	 * Test output with overwrite false expect exception.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testOutputWithOverwriteFalseExpectException() {
		deleteIfExists();
		ioUtils.ouputStepsToFile(testContent, outputFileLocation, false);
		// Call it again and expect an illegal argument exception
		ioUtils.ouputStepsToFile(testContent, outputFileLocation, false);
	}

	/**
	 * Test output with overwrite two time.
	 */
	@Test
	public void testOutputWithOverwriteTwoTime() {
		deleteIfExists();
		ioUtils.ouputStepsToFile(testContent, outputFileLocation, true);
		ioUtils.ouputStepsToFile(testContent, outputFileLocation, true);
		assertTrue(ioUtils.fileExists(outputFileLocation));
	}

	/**
	 * Read swagger from file.
	 */
	@Test
	public void readOpenAPISpecFromJsonFile() {
		OpenAPI openAPI=ioUtils.readOpenAPISpecFromLocation(inputJsonContractLocation);
		assertNotNull(openAPI);
	}

	/**
	 * Read swagger from file.
	 */
	@Test
	public void readOpenAPISpecFromYamlFile() {
		OpenAPI openAPI=ioUtils.readOpenAPISpecFromLocation(inputYamlContractLocation);
		assertNotNull(openAPI);
	}

	/**
	 * Read swagger from file.
	 */
	@Test
	public void readOpenAPISpecFromURL() {
		OpenAPI openAPI=ioUtils.readOpenAPISpecFromLocation(inputYamlContractLocation);
		assertNotNull(openAPI);
	}

	/**
	 * Tear down.
	 */
	@After
	public void tearDown() {
		deleteIfExists();
	}
}
