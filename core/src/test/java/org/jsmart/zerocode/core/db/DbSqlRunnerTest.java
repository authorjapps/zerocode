package org.jsmart.zerocode.core.db;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;


import org.junit.Before;
import org.junit.Test;


public class DbSqlRunnerTest extends DbTestBase {

	@Before
	public void setUp() throws SQLException {
		super.setUp();
		new QueryRunner().update(conn, "DROP TABLE IF EXISTS SQLTABLE; "
				+ "CREATE TABLE SQLTABLE (ID INTEGER, NAME VARCHAR(20)); "
				+ "INSERT INTO SQLTABLE VALUES (1, 'string 1'); "
				+ "INSERT INTO SQLTABLE VALUES (2, 'string 2');");
	}

	@Test
	public void sqlSelectQueryShouldReturnListOfMap() throws ClassNotFoundException, SQLException {
		List<Map<String, Object>> rows = execute("SELECT ID, NAME FROM SQLTABLE ORDER BY ID DESC", null);
		assertThat(rows.toString(), equalTo(convertDbCase("[{ID=2, NAME=string 2}, {ID=1, NAME=string 1}]")));
	}

	@Test
	public void sqlSelectWithoutResultsShouldReturnEmptyList() throws ClassNotFoundException, SQLException {
		List<Map<String, Object>> rows = execute("SELECT ID, NAME FROM SQLTABLE where ID<0", null);
		assertThat(rows.toString(), equalTo("[]"));
	}

	@Test
	public void multipleSqlSelectShouldReturnTheFirstResultSet() throws ClassNotFoundException, SQLException {
		List<Map<String, Object>> rows = execute("SELECT ID, NAME FROM SQLTABLE where ID=2; SELECT ID, NAME FROM SQLTABLE where ID=1;", null);
		assertThat(rows.toString(), equalTo(convertDbCase("[{ID=2, NAME=string 2}]")));
	}

	@Test
	public void sqlInsertShouldReturnNull() throws ClassNotFoundException, SQLException {
		Object nullRows = execute("INSERT INTO SQLTABLE VALUES (3, 'string 3')", null);
		assertThat(nullRows, nullValue());
		// check rows are inserted
		List<Map<String, Object>> rows = execute("SELECT ID, NAME FROM SQLTABLE ORDER BY ID", new Object[] {});
		assertThat(rows.toString(), equalTo(convertDbCase("[{ID=1, NAME=string 1}, {ID=2, NAME=string 2}, {ID=3, NAME=string 3}]")));
	}

	@Test
	public void executeWithParametersShouldAllowNulls() throws SQLException {
		execute("INSERT INTO SQLTABLE VALUES (?, ?)", new Object[] { 4, null });
		List<Map<String, Object>> rows = execute("SELECT ID, NAME FROM SQLTABLE where ID = ?", new Object[] { 4 });
		assertThat(rows.toString(), equalTo(convertDbCase("[{ID=4, NAME=null}]")));
	}

}
