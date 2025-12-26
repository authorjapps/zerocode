package org.jsmart.zerocode.integrationtests.db;
import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("db_test.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class DbSqlExecutorInMemoryH2WithSQLFILETest {

	@Test
	@Scenario("integration_test_files/db/db_sql_execute_from_SQL_file.json")
	public void testDbSqlExecute_SQL_FILE() throws Exception {
	}

}
