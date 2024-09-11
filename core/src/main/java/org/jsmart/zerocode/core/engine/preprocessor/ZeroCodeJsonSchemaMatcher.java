package org.jsmart.zerocode.core.engine.preprocessor;

import com.fasterxml.jackson.databind.JsonNode;

public interface ZeroCodeJsonSchemaMatcher {

    public boolean ismatching(JsonNode JsonFile , JsonNode JsonSchema);


}
