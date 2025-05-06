package org.jsmart.zerocode.core.di.provider;


import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.inject.Provider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CsvParserProvider implements Provider<JacksonCsvParserAdapter> {
    private static final Logger logger = LoggerFactory.getLogger(CsvParserProvider.class);
    private static final JacksonCsvParserAdapter instance;
    public static final String LINE_SEPARATOR = "\n";
    private static final String CARRIAGE_RETURN = "\r";

    static {
        final CsvSchema schema = createCsvSchema();
        final CsvMapper csvMapper = new CsvMapper();
        csvMapper.enable(CsvParser.Feature.TRIM_SPACES);
        csvMapper.enable(CsvParser.Feature.ALLOW_TRAILING_COMMA);
        csvMapper.registerModule(new SimpleModule()
                .addDeserializer(String.class, new CsvParserConfig.CustomStringDeserializer()));
        final ObjectReader mapper =  csvMapper
                .enable(CsvParser.Feature.TRIM_SPACES)
                .readerFor(String[].class)
                .with(schema);
        instance = new JacksonCsvParserAdapter(mapper);
    }

    @Override
    public JacksonCsvParserAdapter get() {
        return instance;
    }

    public String[] parseLine(final String line) {
        try {
            return instance.parseLine(sanitizeLine(line));
        } catch (final IOException e) {
            logger.warn("Failed to parse line: {}", line, e);
            return new String[0];
        }
    }

    private String sanitizeLine(final String line) {
        if (StringUtils.isNotBlank(line) && !line.contains(CARRIAGE_RETURN)) {
            return line;
        }
        return line.replace(CARRIAGE_RETURN, StringUtils.SPACE);
    }

    private static CsvSchema createCsvSchema() {
        return CsvSchema.builder()
                .setColumnSeparator(',')
                .setQuoteChar('\'')
                .setNullValue("")
                .setLineSeparator(LINE_SEPARATOR)
                .build();
    }

}
