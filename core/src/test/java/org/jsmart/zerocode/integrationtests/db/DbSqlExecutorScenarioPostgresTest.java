package org.jsmart.zerocode.integrationtests.db;
import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("db_test_postgres.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class DbSqlExecutorScenarioPostgresTest {

	// Note: Spin up the DB container before running this test: docker/compose/pg_compose.yml
	
    @Test
	@Scenario("integration_test_files/db/db_csv_load_with_headers_postgres.json")
	public void testDbCsvLoadWithHeaders() throws Exception {
	}

    @Test
	@Scenario("integration_test_files/db/db_sql_execute_postgres.json")
	public void testDbSqlExecute() throws Exception {
	}

}
