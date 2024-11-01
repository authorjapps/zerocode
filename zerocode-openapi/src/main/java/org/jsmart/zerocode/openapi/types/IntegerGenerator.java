package org.jsmart.zerocode.openapi.types;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.models.media.Schema;

public class IntegerGenerator extends PrimitiveGenerator {
	public IntegerGenerator(String name, Schema<?> schema) {
		super(name, schema);
	}

	@Override
	public JsonNode generateJsonValue() {
		if (hasEnum())
			return new ObjectMapper().createObjectNode().numberNode(Long.valueOf(getEnumItem().toString()));
		else
			return new ObjectMapper().createObjectNode().textNode("${RANDOM.NUMBER:8}");
		// TODO core enhancement Generate unquoted random numbers
		// The value generated for random numbers is a string. Could this cause problems problems
		// in accepting these values by the api server depending on its serialization approach
	}

}
