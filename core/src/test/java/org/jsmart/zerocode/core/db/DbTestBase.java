package org.jsmart.zerocode.core.db;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.dbutils.DbUtils;
import org.jsmart.zerocode.core.di.main.ApplicationMainModule;
import org.jsmart.zerocode.core.guice.ZeroCodeGuiceTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Base class for the unit DB test classes: manages connections,
 * execution of queries and DBMS specific features
 */
public abstract class DbTestBase {
	@Rule
	public ZeroCodeGuiceTestRule guiceRule = new ZeroCodeGuiceTestRule(this, DbTestBase.ZeroCodeTestModule.class);

	// Subclasses must use JukitoRunner
    public static class ZeroCodeTestModule extends AbstractModule {
        @Override
        protected void configure() {
            ApplicationMainModule applicationMainModule = new ApplicationMainModule("db_test.properties");
            install(applicationMainModule);
        }
    }

	@Inject
	@Named("db.driver.url") protected String url;

	@Inject(optional = true)
	@Named("db.driver.user") protected String user;
	
	@Inject(optional = true)
	@Named("db.driver.password") protected String password;

	protected Connection conn; // managed connection for each test
	protected boolean isPostgres = false; // set by each connection, to allow portable assertions

	@Before
	public void setUp() throws SQLException {
		conn = connect();
	}

	@After
	public void tearDown() throws Exception {
		DbUtils.closeQuietly(conn);
	}
	
	protected Connection connect() throws SQLException {
		isPostgres = url.startsWith("jdbc:postgresql:");
		return DriverManager.getConnection(url, user, password);
	}

	protected List<Map<String, Object>> execute(String sql, Object[] params) throws SQLException {
		DbSqlRunner runner = new DbSqlRunner(conn);
		return runner.execute(sql, params);
	}
	
	// Table and columns in all tests are uppercase because H2 stores uppercase by default.
	// But postgres stores lowercase, so some expected strings need case conversion
	protected String convertDbCase(String value) {
		return isPostgres ? value.toLowerCase() : value;
	}

}