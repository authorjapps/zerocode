package org.jsmart.zerocode.core.db;

import com.google.inject.Inject;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jsmart.zerocode.core.di.provider.CsvParserProvider;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

public class DbCsvLoaderTest extends DbTestBase{
	
	private DbCsvLoader loader;
	
	@Inject
	CsvParserProvider csvParser;
	
	@Override
	public void setUp() throws SQLException {
		super.setUp();
		loader = new DbCsvLoader(conn, csvParser);
		execute("DROP TABLE IF EXISTS CSVTABLE; "
				+ "CREATE TABLE CSVTABLE (ID INTEGER, NAME VARCHAR(20), STATUS BOOLEAN)", null);
	}
	
	private int loadCsv(String table, String[] lines, boolean withHeaders, String nullString) throws SQLException {
		List<String> linesList = Arrays.asList(lines);
		return loader.loadCsv(table, linesList, withHeaders, nullString);
	}
	
	private void assertLoaded(int count, String expected) throws SQLException {
		List<Map<String, Object>> rows = execute("SELECT ID,NAME,STATUS FROM CSVTABLE ORDER BY ID NULLS FIRST", null);
		assertThat(rows.toString(), equalTo(convertDbCase(expected)));
		assertThat(rows.size(), equalTo(count));
	}
	
	@Test
	public void testLoadSimpleCsvWithoutHeaders() throws SQLException {
		int count = loadCsv("CSVTABLE", new String[] { "101,me,false", "102,you,true" }, false, "");
		assertLoaded(count, "[{ID=101, NAME=me, STATUS=false}, {ID=102, NAME=you, STATUS=true}]");
	}

	@Test
	public void testLoadSimpleCsvWithHeaders() throws SQLException {
		int count = loadCsv("CSVTABLE", new String[] { "ID,NAME,STATUS", "101,me,false", "102,you,true" }, true, "");
		assertLoaded(count, "[{ID=101, NAME=me, STATUS=false}, {ID=102, NAME=you, STATUS=true}]");
	}

	@Test
	public void testLoadCsvIsNotCleanInsert() throws SQLException {
		loadCsv("CSVTABLE", new String[] { "101,me,false" }, false, "");
		loadCsv("CSVTABLE", new String[] { "103,other,true" }, false, "");
		assertLoaded(2, "[{ID=101, NAME=me, STATUS=false}, {ID=103, NAME=other, STATUS=true}]");
	}

	@Test
	public void whenNoDataRows_thenReturnZero() throws SQLException {
		int count = loader.loadCsv("CSVTABLE", null, false, "");
		assertLoaded(count, "[]");
		count = loadCsv("CSVTABLE", new String[] { }, false, ""); //noheaders norows
		assertLoaded(count, "[]");
		count = loadCsv("CSVTABLE", new String[] {"ID,NAME,STATUS" }, true, ""); //headers norows
		assertLoaded(count, "[]");
		count = loadCsv("CSVTABLE", new String[] { }, true, ""); //headers missing
		assertLoaded(count, "[]");
	}

	@Test
	public void whenCsvValuesContainSpaces_thenValuesAreTrimmed() throws SQLException {
		loadCsv("CSVTABLE", new String[] { "  ID ,  \t  NAME  \r  , STATUS  ", "  101 ,\tmy\t  name\r, false  " }, true, "");
		assertLoaded(1, "[{ID=101, NAME=my\t  name, STATUS=false}]");
	}

	@Test
	public void whenNullStringUnset_thenEmptyIsNull() throws SQLException {
		loadCsv("CSVTABLE", new String[] { "  \t  , me ,  \t  ", "102,,true" }, false, "");
		assertLoaded(2, "[{ID=null, NAME=me, STATUS=null}, {ID=102, NAME=null, STATUS=true}]");
	}

	@Test
	public void whenNullStringSet_thenEmptyIsNotNull_AndCaseInsensitive() throws SQLException {
		loadCsv("CSVTABLE", new String[] { "  null  ,me,  NULL  ", "102, ,true" }, false, "null");
		assertLoaded(2, "[{ID=null, NAME=me, STATUS=null}, {ID=102, NAME=, STATUS=true}]");
	}

	@Test
	public void whenRowsHaveDistinctSizes_thenRaiseExceptionWithMessage() throws SQLException {
		RuntimeException e = assertThrows(RuntimeException.class, () -> {
			loadCsv("CSVTABLE", new String[] { "ID,NAME,STATUS", "101,me,true,additional" , "102,you,true" }, true, "");
		});
		assertThat(e.getMessage(), equalTo(
				"Error parsing CSV content to load into table CSVTABLE: Row 2 has 4 columns and should have 3"));
	}
	
	@Test
	public void whenParameterHasWrongType_thenRaiseExceptionWithMessage() throws SQLException {
		RuntimeException e = assertThrows(RuntimeException.class, () -> {
			loadCsv("CSVTABLE", new String[] { "ID,NAME,STATUS", "101,me,true" , "XXXX,you,true" }, true, "");
		});
		assertThat(e.getMessage(), equalTo("Error matching data type of parameters and table columns at CSV row 3"));
	}
	
	@Test
	public void whenInsertFails_thenRaiseExceptionWithMessage() throws SQLException {
		RuntimeException e = assertThrows(RuntimeException.class, () -> {
			loadCsv("CSVTABLE", new String[] { "101,me,true,extra1" , "102,you,true,extra2" }, false, "");
		});
		assertThat(e.getMessage(), equalTo("Error inserting data at CSV row 1"));
	}

}