package org.jsmart.zerocode.core.di.provider;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import jakarta.inject.Provider;

public class CsvParserProvider implements Provider<CsvParser> {
    public static final String LINE_SEPARATOR = "\n";

    @Override
    public CsvParser get() {
        CsvParserSettings settings = createCsvSettings();
        return new CsvParser(settings);
    }

    private CsvParserSettings createCsvSettings() {
        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setDelimiter(",");
        settings.getFormat().setQuote('\'');
        settings.getFormat().setQuoteEscape('\'');
        settings.setEmptyValue("");
        settings.getFormat().setLineSeparator("\n");
        settings.setAutoConfigurationEnabled(false);
        return settings;
    }

}
