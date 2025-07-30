package org.jsmart.zerocode.openapi;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * End to End test to run a sample of the generated scenarios against the real swagger petstore api.
 * The tests are for manual execution only. See the setup and the test script below.
 */
@Ignore
@TargetEnv("e2e_test_files/petstore.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class E2eTest {

	/*
	Setup:
	- Clone and run the swagger petstore using the scripts provided in the resources/e2e_test_files folder
	- Run the integration tests (that will place the generated scenarios to use in this test in the target folder)
	- Comment the Ignore annotation and follow the scripts below (uncomment a scenario in each test).
	
	Pet endpoints:
	- Run test _pet.json. Verify the first (POST): request/response. Save the pet id. The second (PUT) should fail
	  Edit file and set the PUT pet id to the saved value and change the name. Run test. Verify all responses
	- Edit _pet_{petId} and set the url id to the saved value in all 3 operation. Set the queryParams to some value in first operation.
	  Run the test. Verify all responses. Run again, first step should fail because the pet was deleted
	- Run _pet_findByStatus.json. Verify the response has only pets with the same status (sold)
	- Edit _pet_{petId}_uploadImage.json, set the id to 5 (created on startup) and run.
	  Verify the response has a new item in photoUrls: /tmp/inflectorNNNNNN.tmp
	  From inside the container, type this file. Its value must be equal to the body sent in the request
	  
	Other endpoints:
	- Run _store_inventory.json. Verify the responses
	- Run _store_order.json. Verify responses. ShipDate must be the current datetime +00:00
	  (note that zulu format is not supported, dates are generated as localdate)
	- Run _user_createWithList.json. Verify responses
	 */
	@Test
	@Scenario("target/integration_test_generated_files/_pet.json")
	//@Scenario("target/integration_test_generated_files/_pet_{petId}.json")
	//@Scenario("target/integration_test_generated_files/_pet_findByStatus.json")
	//@Scenario("target/integration_test_generated_files/_pet_{petId}_uploadImage.json")
	
	//@Scenario("target/integration_test_generated_files/_store_inventory.json")
	//@Scenario("target/integration_test_generated_files/_store_order.json")
	//@Scenario("target/integration_test_generated_files/_user_createWithList.json")
	public void testE2eScenario() throws Exception {
	}

}
