package org.jsmart.zerocode.core.db;
import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("db_test.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class DbSqlExecutorScenarioTest {

    @Test
	@Scenario("integration_test_files/db/db_sql_execute.json")
	public void testDbSqlExecute() throws Exception {
	}

}
