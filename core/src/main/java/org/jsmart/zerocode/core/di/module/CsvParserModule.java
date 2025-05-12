package org.jsmart.zerocode.core.di.module;

import com.google.inject.Binder;
import com.google.inject.Module;
import jakarta.inject.Singleton;
import org.jsmart.zerocode.core.di.provider.CsvParserInterface;
import org.jsmart.zerocode.core.di.provider.CsvParserProvider;

public class CsvParserModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(CsvParserInterface.class).toProvider(CsvParserProvider.class).in(Singleton.class);
    }
}

