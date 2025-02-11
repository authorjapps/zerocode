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
		return new ObjectMapper().createObjectNode() // only urlencode the enum values
				.textNode(hasEnum() ? encodeIfRequired(getEnumItem().toString()) : getRandomToken());
	}
	
	private String getRandomToken() {
		// Note that we are always generating the current date/time value, that can lead to repeated values in a step
		if ("date-time".equals(getFormat()))
			return "${LOCAL.DATETIME.NOW:yyyy-MM-dd'T'HH:mm:ss}";
		else if ("date".equals(getFormat()))
			return "${LOCAL.DATE.NOW:yyyy-MM-dd}";
		else // for string and other formats fallback
			// RANDOM.STRING always returns the same value inside an step.
			// Workaround: add a number to make values different
			return "${RANDOM.STRING:12}${RANDOM.NUMBER:4}";
	}

}
