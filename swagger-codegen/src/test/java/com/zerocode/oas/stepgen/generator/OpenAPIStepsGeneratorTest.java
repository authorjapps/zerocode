package com.zerocode.oas.stepgen.generator;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import com.zerocode.oas.stepgen.io.IOUtils;

import io.swagger.v3.oas.models.OpenAPI;

@RunWith(SpringRunner.class)
public class OpenAPIStepsGeneratorTest {


	/**
	 * The Class IOUtilsTestTestContextConfiguration.
	 */
	@TestConfiguration
	@ComponentScan(basePackageClasses = OpenAPIStepsGenerator.class)
	static class OpenAPITestContextConfiguration {

		/**
		 * IOUtis.
		 *
		 * @return the IO utils
		 */
		@Bean
		public IOUtils ioUtils() {
			return new IOUtils();
		}
		
		
	}
	@Autowired
	OpenAPIStepsGenerator stepGenerator;

	@Autowired
	IOUtils ioUtils;

	
	OpenAPI openAPI;
	
	@Before
	public void setup() {
		openAPI=ioUtils.readOpenAPISpecFromLocation("petStore.yaml");
	}
	
	@Test
	public void test() {
		assertNotNull(openAPI);
		ScenarioSpec scenario=stepGenerator.transform(openAPI);
		assertNotNull(scenario);
		assertTrue(scenario.getSteps()!=null && scenario.getSteps().size()>1);
	}

}
