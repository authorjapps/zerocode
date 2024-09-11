package org.jsmart.zerocode.core.engine.preprocessor;

import com.fasterxml.jackson.databind.JsonNode;
import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.ScenarioSpec;

public interface ZeroCodeJsonSchemaMatcher {

    public boolean ismatching(JsonNode JsonFile , JsonNode JsonSchema);


}
