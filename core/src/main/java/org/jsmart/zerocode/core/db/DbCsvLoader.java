package org.jsmart.zerocode.core.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang3.StringUtils;
import org.jsmart.zerocode.core.di.provider.CsvParserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Data loading in the database from a CSV external source
 */
class DbCsvLoader {
	private static final Logger LOGGER = LoggerFactory.getLogger(DbCsvLoader.class);
	private Connection conn;
	private CsvParserProvider csvParser;

	public DbCsvLoader(Connection conn, CsvParserProvider csvParser) {
		this.conn = conn;
		this.csvParser = csvParser;
	}

	/**
	 * Loads rows in CSV format (csvLines) into a table in the database
	 * and returns the total number of rows.
	 */
	public int loadCsv(String table, List<String> csvLines, boolean withHeaders, String nullString) throws SQLException {
		if (csvLines == null || csvLines.isEmpty())
			return 0;
		
		List<String[]> lines = parseLines(table, csvLines);

		String[] headers = buildHeaders(lines.get(0), withHeaders);
		List<Object[]> paramset = buildParameters(table, headers, lines, withHeaders, nullString);
		if (paramset.isEmpty()) // can have headers, but no rows
			return 0;

		String sql = buildSql(table, headers, paramset.get(0).length);
		LOGGER.info("Loading CSV using this sql: {}", sql);

		QueryRunner runner = new QueryRunner();
		int insertCount = 0;
		for (int i = 0 ; i < paramset.size(); i++) {
			insertRow(runner, i, sql, paramset.get(i));
			insertCount++;
		}
		LOGGER.info("Total of rows inserted: {}", insertCount);
		return insertCount;
	}
	
	private List<String[]> parseLines(String table, List<String> lines) {
		int numCol = 0; // will check that every row has same columns than the first
		List<String[]> parsedLines = new ArrayList<>();
		for (int i = 0; i<lines.size(); i++) {
			String[] parsedLine = csvParser.parseLine(lines.get(i));
			parsedLines.add(parsedLine);
			if (i == 0) {
				numCol=parsedLine.length;
			} else if (numCol != parsedLine.length) {
				String message = String.format("Error parsing CSV content to load into table %s: "
						+ "Row %d has %d columns and should have %d", table, i + 1, parsedLine.length, numCol);
				LOGGER.error(message);
				throw new RuntimeException(message);
			}
		}
		return parsedLines;
	}

	private String[] buildHeaders(String[] line, boolean withHeaders) {
		return withHeaders ? line : new String[] {};
	}

	private List<Object[]> buildParameters(String table, String[] headers, List<String[]> lines, boolean withHeaders, String nullString) {
		DbValueConverter converter = new DbValueConverter(conn, table);
		List<Object[]> paramset = new ArrayList<>();
		for (int i = withHeaders ? 1 : 0; i < lines.size(); i++) {
			String[] parsedLine = lines.get(i);
			parsedLine = processNulls(parsedLine, nullString);
			Object[] params;
			try {
				params = converter.convertColumnValues(headers, parsedLine);
				LOGGER.info("    row [{}] params: {}", i + 1, Arrays.asList(params).toString());
			} catch (Exception e) { // Not only SQLException as converter also does parsing
				String message = String.format("Error matching data type of parameters and table columns at CSV row %d", i + 1);
				LOGGER.error(message);
				LOGGER.error("Exception message: {}", e.getMessage());
				throw new RuntimeException(message, e);
			}
			paramset.add(params);
		}
		return paramset;
	}

	private String[] processNulls(String[] line, String nullString) {
		for (int i = 0; i < line.length; i++) {
			if (StringUtils.isBlank(nullString) && StringUtils.isBlank(line[i])) {
				line[i] = null;
			} else if (!StringUtils.isBlank(nullString)) {
				if (StringUtils.isBlank(line[i])) // null must be empty string
					line[i] = "";
				else if (nullString.trim().equalsIgnoreCase(line[i].trim()))
					line[i] = null;
			}
		}
		return line;
	}

	private String buildSql(String table, String[] headers, int columnCount) {
		String placeholders = IntStream.range(0, columnCount)
				.mapToObj(i -> "?").collect(Collectors.joining(","));
		return "INSERT INTO " + table 
				+ (headers.length > 0 ? " (" + String.join(",", headers) + ")" : "") 
				+ " VALUES (" + placeholders + ");";
	}
	
	private void insertRow(QueryRunner runner, int rowId, String sql, Object[] params) {
		try {
			runner.update(conn, sql, params);
		} catch (SQLException e) {
			String message = String.format("Error inserting data at CSV row %d", rowId + 1);
			LOGGER.error(message);
			LOGGER.error("Exception message: {}", e.getMessage());
			throw new RuntimeException(message, e);
		}
	}

}