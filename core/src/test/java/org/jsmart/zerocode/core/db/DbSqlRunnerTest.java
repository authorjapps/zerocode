package org.jsmart.zerocode.core.db;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.jsmart.zerocode.core.utils.PropertiesProviderUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DbSqlRunnerTest {

	private static final String DB_PROPERTIES_RESOURCE = "db_test.properties";
	private Connection conn;

	@BeforeClass
	public static void classSetUp() throws FileNotFoundException, SQLException, IOException {
		Connection createConn = connect();
		new QueryRunner().update(createConn, "DROP TABLE IF EXISTS SQLTABLE; "
				+ "CREATE TABLE SQLTABLE (ID INTEGER, NAME VARCHAR(20)); ");
		DbUtils.closeQuietly(createConn);
	}

	@Before
	public void setUp() throws ClassNotFoundException, SQLException, FileNotFoundException, IOException {
		conn = connect();
		new QueryRunner().update(conn, "DELETE FROM SQLTABLE; "
				+ "INSERT INTO SQLTABLE VALUES (1, 'string 1'); "
				+ "INSERT INTO SQLTABLE VALUES (2, 'string 2');");
	}

	@After
	public void tearDown() throws Exception {
		DbUtils.closeQuietly(conn);
	}

	private static Connection connect() throws SQLException, FileNotFoundException, IOException {
		Properties prop = PropertiesProviderUtils.getProperties(DB_PROPERTIES_RESOURCE);
		return DriverManager.getConnection(
				prop.getProperty("db.driver.url"), prop.getProperty("db.driver.user"), prop.getProperty("db.driver.password") );
	}
	
	private List<Map<String, Object>> execute(String sql, Object[] params) throws SQLException {
		DbSqlRunner runner = new DbSqlRunner(conn);
		return runner.execute(sql, params);
	}

	@Test
	public void sqlSelectQueryShouldReturnListOfMap() throws ClassNotFoundException, SQLException {
		List<Map<String, Object>> rows = execute("SELECT ID, NAME FROM SQLTABLE ORDER BY ID DESC", null);
		assertThat(rows.toString(), equalTo("[{ID=2, NAME=string 2}, {ID=1, NAME=string 1}]"));
	}

	@Test
	public void sqlSelectWithoutResultsShouldReturnEmptyList() throws ClassNotFoundException, SQLException {
		List<Map<String, Object>> rows = execute("SELECT ID, NAME FROM SQLTABLE where ID<0", null);
		assertThat(rows.toString(), equalTo("[]"));
	}

	@Test
	public void multipleSqlSelectShouldReturnTheFirstResultSet() throws ClassNotFoundException, SQLException {
		List<Map<String, Object>> rows = execute("SELECT ID, NAME FROM SQLTABLE where ID=2; SELECT ID, NAME FROM SQLTABLE where ID=1;", null);
		assertThat(rows.toString(), equalTo("[{ID=2, NAME=string 2}]"));
	}

	@Test
	public void sqlInsertShouldReturnNull() throws ClassNotFoundException, SQLException {
		Object nullRows = execute("INSERT INTO SQLTABLE VALUES (3, 'string 3')", null);
		assertThat(nullRows, nullValue());
		// check rows are inserted
		List<Map<String, Object>> rows = execute("SELECT ID, NAME FROM SQLTABLE ORDER BY ID", new Object[] {});
		assertThat(rows.toString(), equalTo("[{ID=1, NAME=string 1}, {ID=2, NAME=string 2}, {ID=3, NAME=string 3}]"));
	}

	@Test
	public void executeWithParametersShouldAllowNulls() throws SQLException {
		execute("INSERT INTO SQLTABLE VALUES (?, ?)", new Object[] { 4, null });
		List<Map<String, Object>> rows = execute("SELECT ID, NAME FROM SQLTABLE where ID = ?", new Object[] { 4 });
		assertThat(rows.toString(), equalTo("[{ID=4, NAME=null}]"));
	}

}
