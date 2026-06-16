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
		else // Note that numbers are generated as strings
			return new ObjectMapper().createObjectNode().textNode("${RANDOM.NUMBER:8}");
	}

}
