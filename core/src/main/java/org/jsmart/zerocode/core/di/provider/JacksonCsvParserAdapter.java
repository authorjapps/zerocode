package org.jsmart.zerocode.core.di.provider;

import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.StringReader;

public class JacksonCsvParserAdapter implements CsvParserInterface {

    private final ObjectReader mapper;
    public JacksonCsvParserAdapter(final ObjectReader mapper) {
        this.mapper = mapper;
    }
    @Override
    public String[] parseLine(final String line) throws IOException {
        if (StringUtils.isEmpty(line)) return null ;
        if(line.trim().isEmpty()) {
            return new String[]{ null };
        }
        return mapper.readValue(new StringReader(line));
    }
}
