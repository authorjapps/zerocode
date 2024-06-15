package org.jsmart.zerocode.core.engine.preprocessor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.univocity.parsers.csv.CsvParser;
import jakarta.inject.Inject;
import org.jsmart.zerocode.core.di.main.ApplicationMainModule;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.domain.Step;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.jukito.JukitoRunner;
import org.jukito.TestModule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JukitoRunner.class)
public class ZeroCodeParameterizedProcessorImplTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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


    private ZeroCodeParameterizedProcessorImpl parameterizedProcessor;

    @Before
    public void setUp() {
        parameterizedProcessor = new ZeroCodeParameterizedProcessorImpl(mapper, csvParser);
    }

    @Test
    public void testProcessParameterized_wrongDsl() throws Exception {
        String jsonDocumentAsString = smartUtils
                .getJsonDocumentAsString("unit_test_files/engine_unit_test_jsons/12_scenario_parameterized_wrong_dsl.json");
        ScenarioSpec scenarioSpec = mapper.readValue(jsonDocumentAsString, ScenarioSpec.class);

        expectedException.expectMessage("Scenario spec was invalid. Please check the DSL format");
        ScenarioSpec scenarioSpecResolved = parameterizedProcessor.resolveParameterized(scenarioSpec, 0);
    }

    @Test
    public void testProcessParameterized_values() throws Exception {
        String jsonDocumentAsString = smartUtils
                .getJsonDocumentAsString("unit_test_files/engine_unit_test_jsons/10_scenario_parameterized_values.json");
        ScenarioSpec scenarioSpec = mapper.readValue(jsonDocumentAsString, ScenarioSpec.class);

        ScenarioSpec scenarioSpecResolved = parameterizedProcessor.resolveParameterized(scenarioSpec, 0);
        assertThat(scenarioSpecResolved.getSteps().get(0).getUrl(), is("/anUrl/hello"));

        scenarioSpecResolved = parameterizedProcessor.resolveParameterized(scenarioSpec, 1);
        assertThat(scenarioSpecResolved.getSteps().get(0).getUrl(), is("/anUrl/123"));

    }

    @Test
    public void testProcessParameterized_csv() throws Exception {
        String jsonDocumentAsString = smartUtils
                .getJsonDocumentAsString("unit_test_files/engine_unit_test_jsons/11.1_scenario_parameterized_csv.json");
        ScenarioSpec scenarioSpec = mapper.readValue(jsonDocumentAsString, ScenarioSpec.class);

        ScenarioSpec scenarioSpecResolved = parameterizedProcessor.resolveParameterized(scenarioSpec, 0);
        assertThat(scenarioSpecResolved.getSteps().get(0).getUrl(), is("/anUrl/1/2"));
        assertThat(scenarioSpecResolved.getSteps().get(0).getAssertions().get("status").asInt(), is(200));

        scenarioSpecResolved = parameterizedProcessor.resolveParameterized(scenarioSpec, 1);
        assertThat(scenarioSpecResolved.getSteps().get(0).getUrl(), is("/anUrl/11/22"));
        assertThat(scenarioSpecResolved.getSteps().get(0).getAssertions().get("status").asInt(), is(400));

    }

    @Test
    public void testProcessParameterized_csv_with_named_random() throws Exception {
        String jsonDocumentAsString = smartUtils
                .getJsonDocumentAsString("unit_test_files/engine_unit_test_jsons/11.2_scenario_parameterized_csv_with_named_random.json");
        ScenarioSpec scenarioSpec = mapper.readValue(jsonDocumentAsString, ScenarioSpec.class);

        ScenarioSpec scenarioSpecResolved = parameterizedProcessor.resolveParameterized(scenarioSpec, 0);
        Step step = scenarioSpecResolved.getSteps().get(0);
        assertThat(step.getUrl(), is("/anUrl/${RANDOM.NUMBER}/${RANDOM.NUMBER}"));
        JsonNode queryParams = step.getRequest().get("queryParams");
        assertThat(queryParams.get("id1"), is(queryParams.get("id2")));
        assertThat(queryParams.get("addressId1"), is(queryParams.get("addressId2")));
        assertThat(scenarioSpecResolved.getSteps().get(0).getAssertions().get("status").asInt(), is(200));

        scenarioSpecResolved = parameterizedProcessor.resolveParameterized(scenarioSpec, 1);
        assertThat(scenarioSpecResolved.getSteps().get(0).getUrl(), is("/anUrl/11/22"));
        assertThat(scenarioSpecResolved.getSteps().get(0).getAssertions().get("status").asInt(), is(400));
    }
}