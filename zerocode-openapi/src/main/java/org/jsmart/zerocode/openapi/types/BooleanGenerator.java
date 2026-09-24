package org.jsmart.zerocode.openapi.types;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.models.media.Schema;

public class BooleanGenerator extends PrimitiveGenerator {
	public BooleanGenerator(String name, Schema<?> schema) {
		super(name, schema);
	}

	@Override
	public JsonNode generateJsonValue() {
		int value = new RandomGeneratorWithSeed().nextInt(0, 1);
		return new ObjectMapper().createObjectNode().booleanNode(value == 1);
	}

}
