package org.jsmart.zerocode.integrationtests.db;
import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("db_test.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class DbSqlExecutorScenarioTest {

    @Test
	@Scenario("integration_test_files/db/db_csv_load_with_headers.json")
	public void testDbCsvLoadWithHeaders() throws Exception {
	}

    @Test // same scenario and test database
	@Scenario("integration_test_files/db/db_csv_load_without_headers.json")
	public void testDbCsvLoadWithoutHeaders() throws Exception {
	}

    @Test
	@Scenario("integration_test_files/db/db_sql_execute.json")
	public void testDbSqlExecute() throws Exception {
	}

    // Manual test: error handling.
    // To facilitate the location of the source of possible errors (e.g. a wrong SQL statement),
    // exceptions that occur in the DbSqlExecutor should show:
    // - A log entry with the error message
    // - The stacktrace of the exception to facilitate locating the source
    // - The usual chain of errors and stacktraces produced by zerocode when an step fails
    //
    // Recommended situations for manual test:
    // - Target environment variables are no defined
    // - A syntactically wrong SQL statement in a step
    // - A header that does not correspond with any column when loading data from CSV
    // - A value with the wrong data type (e.g. string in a numeric column) when loading data from CSV
    
}
