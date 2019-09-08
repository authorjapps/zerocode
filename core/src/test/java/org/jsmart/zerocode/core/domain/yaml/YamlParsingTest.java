package org.jsmart.zerocode.core.domain.yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.jsmart.zerocode.core.di.provider.YamlObjectMapperProvider;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.jsmart.zerocode.core.utils.SmartUtils.readYamlAsString;

public class YamlParsingTest {
    ObjectMapper mapper = new YamlObjectMapperProvider().get();

    @Test
    public void testYaml_parsing() throws IOException {

        ScenarioSpec scenarioSpec =
                mapper.readValue(readYamlAsString("unit_test_files/yaml/scenario_get_api_test.yml"),
                ScenarioSpec.class);

        // steps
        assertThat(scenarioSpec.getSteps().size(), is(1));
        assertThat(scenarioSpec.getScenarioName(), is("A simple GET API Scenario"));
        assertThat(scenarioSpec.getSteps().get(0).getUrl(), is("/api/v1/persons/p001"));

        // headers
        assertThat(scenarioSpec.getSteps().get(0).getRequest().get("headers").get("x-api-key").asText(), is("Ama-zing-key"));

        // verifications
        assertThat(scenarioSpec.getSteps().get(0).getVerify().get("status").asInt(), is(200));
        assertThat(scenarioSpec.getSteps().get(0).getVerify().get("body").get("exactMatches").asBoolean(), is(true));
        assertThat(scenarioSpec.getSteps().get(0).getVerify().get("body").get("addresses").get(0).toString(),
                is("{\"type\":\"office\",\"line1\":\"10 Random St\"}"));
        assertThat(scenarioSpec.getSteps().get(0).getVerify().get("body").get("addresses").get(1).toString(),
                is("{\"type\":\"home\",\"line1\":\"300 Random St\"}"));
    }
}
