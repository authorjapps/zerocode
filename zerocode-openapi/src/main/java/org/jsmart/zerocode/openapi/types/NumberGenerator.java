package org.jsmart.zerocode.openapi.types;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.models.media.Schema;

public class NumberGenerator extends PrimitiveGenerator {
	public NumberGenerator(String name, Schema<?> schema) {
		super(name, schema);
	}

	@Override
	public JsonNode generateJsonValue() {
		if (hasEnum())
			return new ObjectMapper().createObjectNode().numberNode(Double.valueOf(getEnumItem().toString()));
		else
			return new ObjectMapper().createObjectNode().textNode("${RANDOM.NUMBER:8}.${RANDOM.NUMBER:4}");
	}

}
