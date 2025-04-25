package org.jsmart.zerocode.core.di.provider;

import com.fasterxml.jackson.databind.ObjectReader;

import java.io.IOException;
import java.io.StringReader;

public class JacksonCsvParserAdapter implements CsvParserInterface {

    private final ObjectReader mapper;
    public JacksonCsvParserAdapter(final ObjectReader mapper) {
        this.mapper = mapper;
    }
    @Override
    public String[] parseLine(final String line) throws IOException {
        return mapper.readValue(new StringReader(line));
    }
}
