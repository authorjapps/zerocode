package org.jsmart.zerocode.openapi;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * End to End test to run the generated scenarios against the real swagger petstore api.
 * The tests are for manual execution only. See the setup and the test script below.
 */
//@Ignore
@TargetEnv("e2e_test_files/petstore.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class E2eTest {

	/*
	Setup:
	- Clone and run the swagger petstore using the scripts provided in the resource folder
	- Run the integration tests (that will place the generated scenarios to use in this test in the target folder)
	- Comment the Ignore annotation and follow the scripts below (uncomment a scenario in each test).
	
	Pet endpoints:
	- Run test _pet.json. Verify the first (POST): request/response. Save the pet id. The second (PUT) should fail
	  Edit file and set the PUT pet id to the saved value. Run test. Verify all responses

	 */
	@Test
	@Scenario("target/integration_test_generated_files/_pet.json")
	public void testE2eScenario() throws Exception {
	}

}
