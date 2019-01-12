package org.jsmart.zerocode.core.di.module;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class PropertiesInjectorModule extends AbstractModule {

    private String hostPropertiesFile;

    public PropertiesInjectorModule(String hostPropertiesFile) {
        this.hostPropertiesFile = hostPropertiesFile;
    }

    @Override
    protected void configure() {
        bind(String.class).annotatedWith(Names.named("HostFileName")).toInstance(hostPropertiesFile);

    }
}