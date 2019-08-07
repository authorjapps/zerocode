package org.jsmart.zerocode.core.di.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Binder;
import com.google.inject.Module;
import javax.inject.Singleton;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;

public class ObjectMapperModule implements Module {

  @Override
  public void configure(Binder binder) {
    binder.bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class).in(Singleton.class);
  }
}
