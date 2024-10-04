package org.jsmart.zerocode.core.db;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.dbutils.DbUtils;
import org.jsmart.zerocode.core.utils.PropertiesProviderUtils;
import org.junit.After;
import org.junit.Before;

/**
 * Base class for the unit DB test classes: manages connections,
 * execution of queries and DBMS specific features
 */
public class DbTestBase {
	
	private static final String DB_PROPERTIES_RESOURCE = "db_test.properties";
	protected Connection conn; // managed connection for each test
	protected boolean isPostgres = false; // set by each connection, to allow portable assertions (postgres is lowercase)

	@Before
	public void setUp() throws ClassNotFoundException, SQLException, FileNotFoundException, IOException {
		conn = connect();
	}

	@After
	public void tearDown() throws Exception {
		DbUtils.closeQuietly(conn);
	}
	
	protected Connection connect() throws SQLException {
		Properties prop = PropertiesProviderUtils.getProperties(DB_PROPERTIES_RESOURCE);
		isPostgres = prop.getProperty("db.driver.url").startsWith("jdbc:postgresql:");
		return DriverManager.getConnection(
				prop.getProperty("db.driver.url"), prop.getProperty("db.driver.user"), prop.getProperty("db.driver.password") );
	}

	protected List<Map<String, Object>> execute(String sql, Object[] params) throws SQLException {
		DbSqlRunner runner = new DbSqlRunner(conn);
		return runner.execute(sql, params);
	}
	
	protected String convertDbCase(String value) {
		return isPostgres ? value.toLowerCase() : value;
	}

}