package org.jsmart.zerocode.core.di.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Defines a contract for parsing a single line of CSV data into a string array.
 * Implementations should handle CSV lines with a specified format, such as
 * comma-separated fields with optional quoting and escaping.
 */
public interface CsvParserInterface {
    /**
     * Parses a single line of CSV data into an array of fields.
     *
     * @param line the CSV line to parse, which may include quoted fields and escaped characters
     * @return an array of strings representing the parsed fields; may be empty if the line is invalid
     * @throws IOException if an error occurs during parsing, such as malformed CSV data
     */
    String[] parseLine(final String line) throws IOException;
}
