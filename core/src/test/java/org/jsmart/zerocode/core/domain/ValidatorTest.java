package org.jsmart.zerocode.core.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import org.jsmart.zerocode.core.di.main.ApplicationMainModule;
import org.jsmart.zerocode.core.guice.ZeroCodeGuiceTestRule;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ValidatorTest {
    @Rule
    public ZeroCodeGuiceTestRule guiceRule = new ZeroCodeGuiceTestRule(this, ValidatorTest.ZeroCodeTestModule.class);

    public static class ZeroCodeTestModule extends AbstractModule {
        @Override
        protected void configure() {
            ApplicationMainModule applicationMainModule = new ApplicationMainModule("config_hosts_test.properties");

            /* Finally install the main module */
            install(applicationMainModule);
        }
    }

    @Inject
    SmartUtils smartUtils;

    @Inject
    private ObjectMapper mapper;


    @Test
    public void testValidator_SerDe()throws Exception {
        String json =
                smartUtils.getJsonDocumentAsString("unit_test_files/engine_unit_test_jsons/13_validator_key_value_pair.json");
        Validator validator = mapper.readValue(json, Validator.class);

        assertThat(validator.getField(), is("foo"));
        assertThat(validator.getValue().asText(), is("bar"));
    }

    @Test
    public void testValidators_arraySerDe()throws Exception {
        String json =
                smartUtils.getJsonDocumentAsString("unit_test_files/engine_unit_test_jsons/14_validator_key_value_array.json");
        Validator[] validatorArray = mapper.readValue(json, Validator[].class);

        List<Validator> validators = Arrays.asList(validatorArray);
        assertThat(validators.get(0).getField(), is("foo"));
        assertThat(validators.get(0).getValue().asText(), is("bar"));

        assertThat(validators.get(1).getField(), is("age"));
        assertThat(validators.get(1).getValue().asInt(), is(23));

        assertThat(validators.get(2).getField(), is("address"));
        assertThat(validators.get(2).getValue().toString(), is("{\"line1\":\"East Croydon\",\"postcode\":\"ECY\"}"));
    }
}