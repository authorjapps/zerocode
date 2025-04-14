package org.jsmart.zerocode.core.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.univocity.parsers.csv.CsvParser;

/**
 * Data loading into the database from a CSV external source.
 */
public class DbCsvLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(DbCsvLoader.class);

    private final Connection conn;
    private final CsvParser csvParser;

    public DbCsvLoader(Connection conn, CsvParser csvParser) {
        this.conn = conn;
        this.csvParser = csvParser;
    }

    /**
     * Loads rows in CSV format (csvLines) into a database table
     * and returns the total number of rows inserted.
     */
    public int loadCsv(String table, List<String> csvLines, boolean withHeaders, String nullString) throws SQLException {
        if (csvLines == null || csvLines.isEmpty()) {
            return 0;
        }

        List<String[]> parsedLines = parseLines(table, csvLines);
        String[] headers = extractHeaders(parsedLines, withHeaders);
        List<Object[]> paramSet = buildParameterSet(table, headers, parsedLines, withHeaders, nullString);

        if (paramSet.isEmpty()) {
            return 0; // Can have headers but no rows
        }

        String sql = buildInsertQuery(table, headers, paramSet.get(0).length);
        LOGGER.info("Executing SQL: {}", sql);

        return executeInserts(sql, paramSet);
    }

    private List<String[]> parseLines(String table, List<String> csvLines) {
        int columnCount = -1;
        List<String[]> parsedLines = new ArrayList<>();

        for (int i = 0; i < csvLines.size(); i++) {
            String[] parsedLine = csvParser.parseLine(csvLines.get(i));
            parsedLines.add(parsedLine);

            if (i == 0) {
                columnCount = parsedLine.length;
            } else if (parsedLine.length != columnCount) {
                String error = String.format("CSV Parsing error: Row %d has %d columns, expected %d", i + 1, parsedLine.length, columnCount);
                LOGGER.error(error);
                throw new IllegalArgumentException(error);
            }
        }
        return parsedLines;
    }

    private String[] extractHeaders(List<String[]> parsedLines, boolean withHeaders) {
        return withHeaders ? parsedLines.get(0) : new String[0];
    }

    private List<Object[]> buildParameterSet(String table, String[] headers, List<String[]> lines, boolean withHeaders, String nullString) {
        DbValueConverter converter = new DbValueConverter(conn, table);
        List<Object[]> paramSet = new ArrayList<>();

        for (int i = (withHeaders ? 1 : 0); i < lines.size(); i++) {
            String[] line = processNullValues(lines.get(i), nullString);

            try {
                Object[] params = converter.convertColumnValues(headers, line);
                paramSet.add(params);
            } catch (Exception e) {
                String error = String.format("Error converting data on row %d", i + 1);
                LOGGER.error(error, e);
                throw new RuntimeException(error, e);
            }
        }
        return paramSet;
    }

    private String[] processNullValues(String[] line, String nullString) {
        for (int i = 0; i < line.length; i++) {
            if (StringUtils.isBlank(line[i])) {
                line[i] = StringUtils.equalsIgnoreCase(line[i], nullString) ? null : "";
            }
        }
        return line;
    }

    private String buildInsertQuery(String table, String[] headers, int columnCount) {
        String placeholders = IntStream.range(0, columnCount)
                                       .mapToObj(i -> "?")
                                       .collect(Collectors.joining(", "));
        String columns = headers.length > 0 ? " (" + String.join(", ", headers) + ")" : "";
        return String.format("INSERT INTO %s%s VALUES (%s);", table, columns, placeholders);
    }

    private int executeInserts(String sql, List<Object[]> paramSet) throws SQLException {
        QueryRunner runner = new QueryRunner();
        int insertCount = 0;

        for (Object[] params : paramSet) {
            try {
                runner.update(conn, sql, params);
                insertCount++;
            } catch (SQLException e) {
                String error = String.format("Error inserting data for row %d", insertCount + 1);
                LOGGER.error(error, e);
                throw new SQLException(error, e);
            }
        }
        LOGGER.info("Total rows inserted: {}", insertCount);
        return insertCount;
    }
}