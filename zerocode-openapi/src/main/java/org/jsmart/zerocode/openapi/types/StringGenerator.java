package org.jsmart.zerocode.openapi.types;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.models.media.Schema;

public class StringGenerator extends PrimitiveGenerator {
	public StringGenerator(String name, Schema<?> schema) {
		super(name, schema);
	}

	@Override
	public JsonNode generateJsonValue() {
		return new ObjectMapper().createObjectNode()
				.textNode(hasEnum() ? getEnumItem().toString() : getRandomToken());
	}

	@Override
	public JsonNode generateUrlEncodedJsonValue() {
		return new ObjectMapper().createObjectNode() // only urlencode the enum values
				.textNode(hasEnum() ? urlEncode(getEnumItem().toString()) : getRandomToken());
	}
	
	private String getRandomToken() {
		return "${RANDOM.STRING:12}";
	}

}
