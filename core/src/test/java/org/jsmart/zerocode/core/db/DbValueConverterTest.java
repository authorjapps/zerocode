package org.jsmart.zerocode.core.db;

import org.junit.Ignore;
import org.junit.Test;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

public class DbValueConverterTest extends DbTestBase {

	@Test
	public void convertBasicDataTypeValues() throws SQLException {
		doTestConversion("", "Btable", "VINT INTEGER, VCHAR VARCHAR(20), VBOOL BOOLEAN, EXTRA CHAR(1)",
				new String[] { "Vint", "Vchar", "Vbool" },
				new String[] { "101", "astring", "true" },
				"[{VINT=101, VCHAR=astring, VBOOL=true, EXTRA=null}]");
	}

	@Test
	public void convertNullAndBitValues() throws SQLException {
		doTestConversion("", "NTABLE", "VINT INT, VCHAR VARCHAR(20), VBOOL BOOLEAN",
				new String[] { "VINT", "VCHAR", "VBOOL" }, 
				new String[] { null, null, "1" }, // incl. alternate boolean
				"[{VINT=null, VCHAR=null, VBOOL=true}]");
	}

	@Test
	public void convertDecimalAndFloatValues() throws SQLException {
		doTestConversion("", "FTABLE", "VEXACT NUMERIC(10,0), VDEC DECIMAL(10,2), VFLOAT FLOAT, VREAL REAL",
				new String[] { "VEXACT", "VDEC", "VFLOAT", "VREAL" },
				new String[] { "102", "123.45", "234.56", "3.4561E+2" },
				"[{VEXACT=102, VDEC=123.45, VFLOAT=234.56, VREAL=345.61}]");
	}

	@Ignore
	@Test
	public void convertDateAndTimeValues() throws SQLException {
		List<Map<String, Object>> rows = doTestConversion("", "DTABLE", "VTS1 TIMESTAMP, VTS2 TIMESTAMP, VTIME TIME, VDATE DATE",
				new String[] { "VTS1", "VTS2", "VTIME", "VDATE" }, 
				new String[] { "2024-09-04T08:01:02.456+0300", "2024-09-04T08:01:02+0300", "08:01:02+0300", "2024-09-04" },
				null); 
		// assert individually to allow compare with GMT time (not local)
		assertThat(gmtTimestamp((Date) rows.get(0).get("VTS1")), equalTo("2024-09-04T05:01:02.456"));
		assertThat(gmtTimestamp((Date) rows.get(0).get("VTS2")), equalTo("2024-09-04T05:01:02.000"));
		assertThat(gmtTimestamp((Date) rows.get(0).get("VTIME")), equalTo("1970-01-01T05:01:02.000"));
		assertThat(rows.get(0).get("VDATE").toString(), "2024-09-04", equalTo(rows.get(0).get("VDATE").toString()));
	}

	@Test
	public void convertWithMixedCaseColumnName() throws SQLException {
		// Uses a date type to ensure that is the converter who tries making the conversion, not the driver
		// (neither H2 nor Postgres drivers do conversion of dates)
		List<Map<String, Object>> rows = doTestConversion("", "ITable", "VDATE DATE",
				new String[] { "VDate" }, 
				new String[] { "2024-09-04" },
				"[{VDATE=2024-09-04}]");
		assertThat(rows.get(0).get("VDATE").toString(), equalTo("2024-09-04"));
	}

	@Test
	public void whenNoColumnsSpecified_ThenAllTableColumns_AreIncluded() throws SQLException {
		doTestConversion("", "OTABLE", "VINT SMALLINT, VCHAR VARCHAR(20)", 
				null, 
				new String[] { "101", "astring" },
				"[{VINT=101, VCHAR=astring}]");
	}

	@Test
	public void whenNoColumnsSpecified_AndColumnCountMismatch_ThenConversionFails() throws SQLException {
		assertThrows(SQLException.class, () -> {
			doTestConversion("", "FMTABLE", "VINT BIGINT", 
				null, 
				new String[] { "101", "999" },
				"[{VINT=101}]");
		});
	}

	@Test
	public void whenColumnNotFound_ThenConversionFails() throws SQLException {
		assertThrows(SQLException.class, () -> {
			doTestConversion("", "FCTABLE", "VINT INTEGER, VCHAR VARCHAR(20)", 
					new String[] { "VINT", "NOTEXISTS" },
					new String[] { "101", "astring" }, 
					"[{VINT=101, VCHAR=notexists}]");
		});
	}
	
	@Test
	public void whenValueHasWrongFormat_ThenConversionFails() throws SQLException {
		assertThrows(SQLException.class, () -> {
			doTestConversion("", "FVTABLE", "VTS TIMESTAMP", 
					new String[] { "VTS" },
					new String[] { "notadate" }, 
					"[{VTS=notadate}]");
		});
	}
	
	// Failures due to problems with metadata.
	// Simulates failures getting metadata so that the conversion is left to
	// be done by the driver.
	// - Below tests will pass because the H2 driver converts numeric values
	// - but fail if driver is changed to Postgres (does not convert numeric), skipped
	
	@Test
	public void whenMetadataNotFound_ThenConversions_AreUpToTheDriver_WithColumns() throws SQLException {
		if (isPostgres)
			return;
		doTestConversion("table", "F1TABLE", "VINT INTEGER, VCHAR VARCHAR(20)", 
				new String[] { "VINT", "VCHAR" },
				new String[] { "101", "astring" }, 
				"[{VINT=101, VCHAR=astring}]");
	}

	@Test
	public void whenMetadataNotFound_ThenConversions_AreUpToTheDriver_WithoutColumns() throws SQLException {
		if (isPostgres)
			return;
		doTestConversion("table", "F2CTABLE", "VINT INTEGER, VCHAR VARCHAR(20)", 
				null, 
				new String[] { "101", "astring" },
				"[{VINT=101, VCHAR=astring}]");
	}

	@Test
	public void whenMetadataFails_ThenConversions_AreUpToTheDriver() throws SQLException {
		if (isPostgres)
			return;
		doTestConversion("conn", "F3TABLE", "VINT INTEGER, VCHAR VARCHAR(20)", 
				new String[] { "VINT", "VCHAR" },
				new String[] { "101", "astring" }, 
				"[{VINT=101, VCHAR=astring}]");
	}
	
	private List<Map<String, Object>> doTestConversion(String failureToSimulate, String table, 
			String ddlTypes, String[] columns, String[] params, String expected) throws SQLException {
		execute("DROP TABLE IF EXISTS " + table + ";" 
			+ " CREATE TABLE " + table + " (" + ddlTypes + ");", null);
		String sql = "INSERT INTO " + table 
				+ (columns != null ? " (" + String.join(",", columns) + ")" : "")
				+ " VALUES (" + placeholders(params.length) + ")";

		DbValueConverter converter = new DbValueConverter(
				"conn".equals(failureToSimulate) ? null : conn,
				"table".equals(failureToSimulate) ? "notexists" : table);
		Object[] converted = converter.convertColumnValues(columns, params);
		execute(sql, converted);

		List<Map<String, Object>> rows = execute("SELECT * FROM " + table, null);
		if (expected != null) // null to check without specified columns
			assertThat(rows.toString(), equalTo(convertDbCase(expected)));
		return rows;
	}

	private String placeholders(int columnCount) {
		String[] placeholders = new String[columnCount];
		Arrays.fill(placeholders, "?");
		return String.join(",", placeholders);
	}

	private String gmtTimestamp(Date dt) {
		java.text.DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		return df.format(dt);
	}
	
}
