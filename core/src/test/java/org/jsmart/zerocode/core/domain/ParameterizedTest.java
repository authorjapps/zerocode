package org.jsmart.zerocode.core.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.univocity.parsers.csv.CsvParser;
import org.jsmart.zerocode.core.di.main.ApplicationMainModule;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.jukito.JukitoRunner;
import org.jukito.TestModule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(JukitoRunner.class)
public class ParameterizedTest {

    public static class JukitoModule extends TestModule {
        @Override
        protected void configureTest() {
            ApplicationMainModule applicationMainModule = new ApplicationMainModule("config_hosts_test.properties");
            install(applicationMainModule);
        }
    }

    @Inject
    SmartUtils smartUtils;

    @Inject
    private ObjectMapper mapper;

    @Inject
    private CsvParser csvParser;

    @Test
    public void testSerDe_valueSource() throws Exception {
        String jsonDocumentAsString =
                smartUtils.getJsonDocumentAsString("unit_test_files/engine_unit_test_jsons/08_parameterized.json");
        Parameterized parameterized = mapper.readValue(jsonDocumentAsString, Parameterized.class);

        assertThat(parameterized.getValueSource(), hasItem("hello"));
        assertThat(parameterized.getValueSource(), hasItem(123));
        assertThat(parameterized.getValueSource(), hasItem(true));

        String actualJson = mapper.writeValueAsString(parameterized);
        assertThat(actualJson, is("{\"valueSource\":[\"hello\",123,true],\"csvSource\":[\"1,        2,        200\",\"11,      22,        400\"]}"));
    }

    @Test
    public void testSerDe_csvSource() throws Exception {
        String jsonDocumentAsString =
                smartUtils.getJsonDocumentAsString("unit_test_files/engine_unit_test_jsons/08_parameterized.json");
        Parameterized parameterized = mapper.readValue(jsonDocumentAsString, Parameterized.class);

        assertThat(parameterized.getCsvSource(), hasItem("1,        2,        200"));
        assertThat(parameterized.getCsvSource(), hasItem("11,      22,        400"));
    }

}