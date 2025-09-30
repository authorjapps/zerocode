package org.jsmart.zerocode.core.di;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.jsmart.zerocode.core.di.main.ApplicationMainModule;
import org.jsmart.zerocode.core.guice.ZeroCodeGuiceTestRule;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
//this config file is to test backword compatibility, remove this test only after release 1.2.9 or later releases
public class ApplicationMainModuleBackwordCompatibilityTest {
    @Rule
    public ZeroCodeGuiceTestRule guiceRule = new ZeroCodeGuiceTestRule(this, ApplicationMainModuleBackwordCompatibilityTest.ZeroCodeTestModule.class);
    public static class ZeroCodeTestModule extends AbstractModule {
        @Override
        protected void configure() {
            ApplicationMainModule applicationMainModule = new ApplicationMainModule("config_hosts_test_backword_compatibility.properties");

            /* Finally install the main module */
            install(applicationMainModule);
        }
    }

    @Inject
    SmartUtils smartUtils;

    @Inject
    @Named("restful.application.endpoint.host")
    private String host;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Test
    public void testGetItRight_Guice() throws Exception {
        assertThat(smartUtils.getItRight(), notNullValue());
    }

    @Test
    public void willInject_host() throws Exception {
        assertThat(host, is("http://localhost-test-backword-compatibility"));
    }

}