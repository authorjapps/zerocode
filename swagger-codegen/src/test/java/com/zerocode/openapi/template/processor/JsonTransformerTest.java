package com.zerocode.openapi.template.processor;

import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "/test.properties")
public class JsonTransformerTest {
	
	@TestConfiguration
	@ComponentScan(basePackageClasses =  {JsonTransformer.class})
	static class Config{
		
	}

	@Autowired
	JsonTransformer transformer;
	
	@MockBean
	Map<String,String> templateLocation;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTransform() {
		transformer.transform();
	}

}
