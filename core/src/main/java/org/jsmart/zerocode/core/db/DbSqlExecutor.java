package org.jsmart.zerocode.core.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interaction with a database using SQL to read/write
 * Requires the appropriated connection data in the target environment
 * properties, see src/test/resources/db_test.properties
 */
public class DbSqlExecutor {
	private static final Logger LOGGER = LoggerFactory.getLogger(DbSqlExecutor.class);
	public static final String SQL_RESULTS_KEY = "rows";
	
	@Inject
	@Named("db.driver.url") private String url;

	@Inject(optional = true)
	@Named("db.driver.user") private String user;
	
	@Inject(optional = true)
	@Named("db.driver.password") private String password;
	
	/**
	 * The EXECUTE operation returns the records retrieved by the SQL specified in the request 
	 * under the key "rows" (select) or an empty object (insert, update)
	 */
	public Map<String, Object> EXECUTE(DbSqlRequest request) {
		return execute(request);
	}
	
	public Map<String, Object> execute(DbSqlRequest request) {
		Connection conn = createAndGetConnection();
		try {
			LOGGER.info("Execute SQL, request -> {} ", request);
			DbSqlRunner runner = new DbSqlRunner(conn);
			List<Map<String, Object>> results = runner.execute(request.getSql(), request.getSqlParams());
			Map<String, Object> response = new HashMap<>();
			if (results == null) { // will return empty node, use "verify":{}
				response.put(SQL_RESULTS_KEY, new ObjectMapper().createObjectNode());
			} else {
				response.put(SQL_RESULTS_KEY, results);
			}
			return response;
		} catch (SQLException e) {
			LOGGER.error("Failed to execute SQL", e);
			throw new RuntimeException(e);
		} finally {
			closeConnection(conn);
		}
	}

	/**
	 * Returns a new JDBC connection using DriverManager.
	 * Override this method in case you get the connections using another approach
	 * (e.g. DataSource)
	 */
	protected Connection createAndGetConnection() {
		LOGGER.info("Create and get connection, url: {}, user: {}", url, user);
		try {
			return DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			LOGGER.error("Failed to create connection", e);
			throw new RuntimeException(e);
		}
	}

	protected void closeConnection(Connection conn) {
		DbUtils.closeQuietly(conn);
	}

}