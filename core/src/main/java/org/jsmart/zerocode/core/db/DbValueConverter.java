package org.jsmart.zerocode.core.db;

import static org.apache.commons.lang3.time.DateUtils.parseDate;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Conversion of string values to be inserted in the database
 * into objects compatible with the java sql type of the target columns.
 */
public class DbValueConverter {
	private static final Logger LOGGER = LoggerFactory.getLogger(DbSqlExecutor.class);

	private Connection conn;
	private String table;
	private DatabaseMetaData databaseMetaData;
	public Map<String, Integer> columnTypes; // java.sql.Types

	public DbValueConverter(Connection conn, String table) {
		this.conn = conn;
		this.table = table;
		try {
			initializeMetadata();
		} catch (Exception e) {
			logInitializeError();
		}
	}

	private void initializeMetadata() throws SQLException {
		LOGGER.info("Metadata initialization for table: {}", table);
		columnTypes = new LinkedHashMap<>(); // must keep column order
		databaseMetaData = conn.getMetaData();

		table = convertToStoredCase(table); // to locate table name in metadata
		LOGGER.info("Database storesLowerCaseIdentifiers={}, storesUpperCaseIdentifiers={}",
				databaseMetaData.storesLowerCaseIdentifiers(), databaseMetaData.storesUpperCaseIdentifiers());

		try (ResultSet rs = databaseMetaData.getColumns(null, null, table, "%")) {
			while (rs.next()) {
				String storedName = rs.getString("COLUMN_NAME");
				int typeValue = rs.getInt("DATA_TYPE");
				// internally, key is lowercase to allow case insensitive lookups
				columnTypes.put(storedName.toLowerCase(), typeValue);
			}
		}
		LOGGER.info("Mapping from java columns to sql types: {}", columnTypes.toString());
		if (columnTypes.isEmpty())
			logInitializeError();
	}

	private String convertToStoredCase(String identifier) throws SQLException {
		if (databaseMetaData.storesLowerCaseIdentifiers()) // e.g. Postgres
			identifier = identifier.toLowerCase();
		else if (databaseMetaData.storesUpperCaseIdentifiers()) // e.g. H2
			identifier = identifier.toUpperCase();
		return identifier;
	}

	private void logInitializeError() {
		LOGGER.error("Initialization of metadata for table {} failed. "
				+ "Errors may appear when matching query parameters to their data types", table);
	}
	
	/**
	 * Given an array of column names and other array with their corresponding values (as strings),
	 * transforms each value to the compatible data type that allow to be inserted in the database.
	 * If the column names are missing, uses all columns in the current table as the column names.
	 */
	Object[] convertColumnValues(String[] columns, String[] values) {
		if (ArrayUtils.isEmpty(columns)) // if no specified, use all columns in the table
			columns = columnTypes.keySet().toArray(new String[0]);

		Object[] converted = new Object[values.length];
		for (int i = 0; i < values.length; i++) {
			converted[i] = i < columns.length && i < values.length 
					? convertColumnValue(columns[i], values[i])
					: values[i];
		}
		return converted;
	}

	private Object convertColumnValue(String column, String value) {
		try {
			return convertColumnValueWithThrow(column, value);
		} catch (ParseException e) {
			LOGGER.error("Can't convert the data type of value {} at column {}", value, column);
			return value;
		}
	}

	/**
	 * Converts the string representation of a data type value into the appropriate simple SQL data type.
	 * If a data type is not handled by this method (or is string), returns the input string value as fallback.
	 * 
	 * See table B-1 in JDBC 4.2 Specification
	 */
	private Object convertColumnValueWithThrow(String column, String value) throws ParseException {
		if (value == null)
			return null;
		if (!columnTypes.containsKey(column.toLowerCase())) // fallback if no metadata
			return value;
		
		int sqlType = columnTypes.get(column.toLowerCase());
		return convertColumnValueFromJavaSqlType(sqlType, value);
	}
		
	private Object convertColumnValueFromJavaSqlType(int sqlType, String value) throws ParseException {
		switch (sqlType) {
		case java.sql.Types.NUMERIC:
		case java.sql.Types.DECIMAL: return java.math.BigDecimal.valueOf(Double.parseDouble(value));
		
		case java.sql.Types.BIT: //accepts "1" as true (e.g. SqlServer)
		case java.sql.Types.BOOLEAN: return Boolean.valueOf("1".equals(value) ? "true" : value);
		
		case java.sql.Types.TINYINT: return Byte.valueOf(value);
		case java.sql.Types.SMALLINT: return Short.valueOf(value);
		case java.sql.Types.INTEGER: return Integer.valueOf(value);
		case java.sql.Types.BIGINT: return Long.valueOf(value);
		
		case java.sql.Types.REAL: return Float.valueOf(value);
		case java.sql.Types.FLOAT: return Double.valueOf(value);
		case java.sql.Types.DOUBLE: return Double.valueOf(value);
		
		case java.sql.Types.DATE: return new java.sql.Date(parseDate(value, getDateFormats()).getTime());
		case java.sql.Types.TIME: return new java.sql.Time(parseDate(value, getTimeFormats()).getTime());
		case java.sql.Types.TIMESTAMP: return new java.sql.Timestamp(parseDate(value, getTimestampFormats()).getTime());
		default:
			return value;
			// Not including: binary and advanced datatypes (e.g. blob, array...)
		}
	}
	
	// Supported date time formats are the common ISO-8601 formats
	// defined in org.apache.commons.lang3.time.DateFormatUtils,
	// as well as their variants that specify milliseconds.
	// This may be made user configurable later, via properties and/or embedded in the payload
	
	private String[] getDateFormats() {
		return new String[] { "yyyy-MM-dd" };
	}

	private String[] getTimeFormats() {
		return new String[] { "HH:mm:ssZ", "HH:mm:ss.SSSZ", "HH:mm:ss", "HH:mm:ss.SSS" };
	}

	private String[] getTimestampFormats() {
		return new String[] { "yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd'T'HH:mm:ss.SSSZ", 
				"yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSS" };
	}

}
