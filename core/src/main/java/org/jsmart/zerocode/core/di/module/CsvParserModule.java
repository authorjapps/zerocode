package org.jsmart.zerocode.core.di.module;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.univocity.parsers.csv.CsvParser;
import javax.inject.Singleton;
import org.jsmart.zerocode.core.di.provider.CsvParserProvider;

public class CsvParserModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(CsvParser.class).toProvider(CsvParserProvider.class).in(Singleton.class);
    }
}

