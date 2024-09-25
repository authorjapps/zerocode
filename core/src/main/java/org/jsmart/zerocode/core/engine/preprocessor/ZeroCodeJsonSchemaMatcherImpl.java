package org.jsmart.zerocode.core.engine.preprocessor;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import java.util.Set;


public class ZeroCodeJsonSchemaMatcherImpl implements ZeroCodeJsonSchemaMatcher {


    @Override
    public boolean ismatching(JsonNode jsonFile, JsonNode schema) {

        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        JsonSchema jsonSchema = factory.getSchema(schema);
        Set<ValidationMessage> errors = jsonSchema.validate(jsonFile);
        return errors.isEmpty();
    }
}

