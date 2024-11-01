package org.jsmart.zerocode.openapi.types;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.v3.oas.models.media.Schema;

/**
 * Base class for all data type generators given their OpenAPI Schema
 * AbstractDataGenerator
 *   AbstractPrimitiveGenerator
 *     IntegerGenerator
 *     StringGenerator
 *     BooleanGenerator
 *   Object Generator
 *   ArrayGenerator
 */
public abstract class DataGenerator {
	protected String name;
	protected Schema<?> schema;

	public DataGenerator(String name, Schema<?> schema) {
		this.name = name;
		this.schema = schema;
	}

	/**
	 * Generates a value to include in a Zerocode step for the appropriate schema data type.
	 */
	public abstract JsonNode generateJsonValue();

	/**
	 * Generates an url encoded value to include in a path for the appropriate schema data type.
	 * The data generator has the responsibility to override this method and perform the encoding
	 * when the value is to be placed in the url (typically for enums).
	 */
	public JsonNode generateUrlEncodedJsonValue() {
		return generateJsonValue();
	}
	
	protected String urlEncode(String value) {
		try {
			return URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}


}
