package org.jsmart.zerocode.core.di.provider;

import com.google.gson.Gson;

import javax.inject.Provider;

public class GsonSerDeProvider implements Provider<Gson> {

    @Override
    public Gson get() {

        return (new Gson());

    }

}
