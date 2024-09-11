package org.jsmart.zerocode.core.engine.preprocessor;

import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.common.Json;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.logbuilder.ZerocodeCorrelationshipLogger;
import org.jsmart.zerocode.core.utils.SmartUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ZeroCodeJsonSchemaMatcherImpl implements ZeroCodeJsonSchemaMatcher {


    @Override
    public boolean ismatching(JsonNode jsonFile, JsonNode schema) {

        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        JsonSchema jsonSchema = factory.getSchema(schema);
        Set<ValidationMessage> errors = jsonSchema.validate(jsonFile);
        return errors.isEmpty();
    }
//    TODO remember to remove the use of ismatching in main function in this CLass
}

