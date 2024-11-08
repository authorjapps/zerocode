package org.jsmart.zerocode.openapi.types;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.v3.oas.models.media.Schema;

/**
 * Base class for all data type generators given their OpenAPI Schema
 * DataGenerator
 *   PrimitiveGenerator
 *     IntegerGenerator
 *     NumberGenerator
 *     StringGenerator (including date and date-time formats)
 *     BooleanGenerator
 *   Object Generator
 *   ArrayGenerator
 */
public abstract class DataGenerator {
	protected String name;
	protected Schema<?> schema;
	// Indicates if values need url encoding, used for query and path parameters.
	// If true, the method encodeIfRequired will return the appropriate encoding
	protected boolean requireUrlEncode;

	public DataGenerator(String name, Schema<?> schema) {
		this.name = name;
		this.schema = schema;
		this.requireUrlEncode = false;
	}

	/**
	 * Generates a value to include in a Zerocode step for the appropriate schema data type.
	 */
	public abstract JsonNode generateJsonValue();

	/**
	 * Sets the generator to require (or not) url encoded values when encodeIfRequired is called
	 */
	public DataGenerator setRequireUrlEncode(boolean value) {
		this.requireUrlEncode = value;
		return this;
	}
	
	public String encodeIfRequired(String value) {
		try {
			return requireUrlEncode ? URLEncoder.encode(value, "UTF-8") : value;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}


}
