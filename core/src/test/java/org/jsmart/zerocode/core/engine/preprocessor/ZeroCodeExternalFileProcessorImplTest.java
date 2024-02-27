package org.jsmart.zerocode.core.engine.preprocessor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.domain.Step;
import org.jsmart.zerocode.core.engine.tokens.ZeroCodeValueTokens;
import org.junit.Test;
import static org.jsmart.zerocode.core.utils.SmartUtils.checkDigNeeded;


import java.io.IOException;
import java.util.Map;

import static com.jayway.jsonpath.JsonPath.read;
import static org.hamcrest.core.Is.is;
import static org.jsmart.zerocode.core.utils.SmartUtils.readJsonAsString;
import static org.junit.Assert.assertThat;

public class ZeroCodeExternalFileProcessorImplTest {
    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();
    private ZeroCodeExternalFileProcessorImpl externalFileProcessor = new ZeroCodeExternalFileProcessorImpl(objectMapper);

    @Test(expected = RuntimeException.class)
    public void test_wrongFileException() throws IOException {
        String jsonAsString = readJsonAsString("unit_test_files/filebody_unit_test/json_step_test_wrong_file_ref.json");
        Map<String, Object> map = objectMapper.readValue(jsonAsString, new TypeReference<Map<String, Object>>() {});

        externalFileProcessor.digReplaceContent(map);
    }

    @Test
    public void test_deepHashMapTraverse() throws IOException {
        String jsonAsString = readJsonAsString("unit_test_files/filebody_unit_test/json_step_test_file.json");
        Map<String, Object> map = objectMapper.readValue(jsonAsString, new TypeReference<Map<String, Object>>() {});

        externalFileProcessor.digReplaceContent(map);
        String resultJson = objectMapper.writeValueAsString(map);

        assertThat(read(resultJson, "$.request.body.id"), is("Emp-No-${RANDOM.NUMBER}"));
        assertThat(read(resultJson, "$.request.headers.secret"), is("passwwrd"));
        assertThat(read(resultJson, "$.assertions.body.age"), is(16));
    }

    @Test
    public void test_deepRecursiveFile() throws IOException {
        String jsonAsString = readJsonAsString("unit_test_files/filebody_unit_test/json_step_test_file_recursive.json");
        Map<String, Object> map = objectMapper.readValue(jsonAsString, new TypeReference<Map<String, Object>>() {});

        externalFileProcessor.digReplaceContent(map);
        String resultJson = objectMapper.writeValueAsString(map);

        assertThat(read(resultJson, "$.request.body.addresses[0].type"), is("corp-office"));
        assertThat(read(resultJson, "$.request.body.addresses[1].type"), is("hr-office"));
    }

    @Test
    public void test_addressArray() throws IOException {
        String jsonAsString = readJsonAsString("unit_test_files/filebody_unit_test/json_step_test_address_array.json");
        Map<String, Object> map = objectMapper.readValue(jsonAsString, new TypeReference<Map<String, Object>>() {});

        externalFileProcessor.digReplaceContent(map);
        String resultJson = objectMapper.writeValueAsString(map);

        assertThat(read(resultJson, "$.request.body.addresses[0].type"), is("corp-office"));
        assertThat(read(resultJson, "$.request.body.addresses[1].type"), is("hr-office"));
    }

    @Test
    public void test_NoExtJsonFile() throws IOException {
        String jsonAsString = readJsonAsString("unit_test_files/filebody_unit_test/json_step_no_ext_json_test_file.json");
        Map<String, Object> map = objectMapper.readValue(jsonAsString, new TypeReference<Map<String, Object>>() {});

        externalFileProcessor.digReplaceContent(map);
        String resultJson = objectMapper.writeValueAsString(map);

        assertThat(read(resultJson, "$.request.body.name"), is("Emma"));
        assertThat(read(resultJson, "$.assertions.body.id"), is(100));
    }

    @Test
    public void test_NoExtFileCheckDigNeeded() throws IOException {
        String jsonAsString = readJsonAsString("unit_test_files/filebody_unit_test/json_step_no_ext_json_test_file.json");
        Step step = objectMapper.readValue(jsonAsString, Step.class);
        assertThat(checkDigNeeded(objectMapper, step, ZeroCodeValueTokens.JSON_PAYLOAD_FILE, ZeroCodeValueTokens.YAML_PAYLOAD_FILE), is(false));

        jsonAsString = readJsonAsString("unit_test_files/filebody_unit_test/json_step_text_node_ext_json_file_test.json");
        step = objectMapper.readValue(jsonAsString, Step.class);
        assertThat(checkDigNeeded(objectMapper, step, ZeroCodeValueTokens.JSON_PAYLOAD_FILE, ZeroCodeValueTokens.YAML_PAYLOAD_FILE), is(true));
    }

    @Test
    public void test_textNode() throws IOException {
        String jsonAsString = readJsonAsString("unit_test_files/filebody_unit_test/json_step_text_request.json");
        Step step = objectMapper.readValue(jsonAsString, Step.class);

        externalFileProcessor.resolveExtJsonFile(step);
        String resultJsonStep = objectMapper.writeValueAsString(step);

        assertThat(read(resultJsonStep, "$.request"), is("I am a simple text"));
    }

    @Test
    public void test_textNodeExtFile() throws IOException {
        String jsonAsString = readJsonAsString("unit_test_files/filebody_unit_test/json_step_text_node_ext_json_file_test.json");
        Step step = objectMapper.readValue(jsonAsString, Step.class);

        Step effectiveStep = externalFileProcessor.resolveExtJsonFile(step);
        String resultJsonStep = objectMapper.writeValueAsString(effectiveStep);

        assertThat(read(resultJsonStep, "$.request.body.id"), is("Emp-No-${RANDOM.NUMBER}"));
        assertThat(read(resultJsonStep, "$.request.headers.api_key"), is("hello key"));
    }

}