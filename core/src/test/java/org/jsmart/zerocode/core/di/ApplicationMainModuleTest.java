package org.jsmart.zerocode.core.di;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.jsmart.zerocode.core.di.main.ApplicationMainModule;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.jukito.JukitoRunner;
import org.jukito.TestModule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JukitoRunner.class)
public class ApplicationMainModuleTest {

    public static class JukitoModule extends TestModule {
        @Override
        protected void configureTest() {
            System.getProperties().put("custom_prop_key", "custom_prop_value");
            System.getProperties().put("key.to.override", "5647");
            ApplicationMainModule applicationMainModule = new ApplicationMainModule("config_hosts_test.properties");

            /* Finally install the main module */
            install(applicationMainModule);
        }
    }

    @Inject
    SmartUtils smartUtils;

    @Inject
    @Named("web.application.endpoint.host")
    private String host;

    @Inject
    @Named("key.to.override")
    private Integer keyToOverride;

    @Inject
    @Named("HOME")
    private String env;

    @Inject
    @Named("custom_prop_key")
    private String prop;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Test
    public void testGetItRight_Guice() throws Exception {
        assertThat(smartUtils.getItRight(), notNullValue());
    }

    @Test
    public void willInject_host() throws Exception {
        assertThat(host, is("http://localhost-test"));
    }

    @Test
    public void willInject_env_value() throws Exception {
        assertThat(env, is(System.getenv("HOME")));
    }

    @Test
    public void willInject_prop_value() throws Exception {
        assertThat(prop, is("custom_prop_value"));
    }

    @Test
    public void willOverride_properties_by_env_value() throws Exception {
        assertThat(keyToOverride, is(5647));
    }
}