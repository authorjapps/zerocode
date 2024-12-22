package org.jsmart.zerocode.core.db;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DbSqlRequest {
	private final String sql;
	private final Object[] sqlParams;

	@JsonCreator
	public DbSqlRequest(
			@JsonProperty("sql") String sql,
			@JsonProperty("sqlParams") Object[] sqlParams) {
		this.sql = sql;
		this.sqlParams = sqlParams;
	}

	public String getSql() {
		return sql;
	}

	public Object[] getSqlParams() {
		return sqlParams;
	}

	@Override
	public String toString() {
		return "Request{" 
				+ "sql=" + sql 
				+ ", sqlParams=" + (sqlParams == null ? "[]" : Arrays.asList(sqlParams).toString())
				+ '}';
	}
}
