package org.jsmart.zerocode.openapi.types;

import java.util.Map.Entry;

import org.jsmart.zerocode.openapi.IDataGeneratorFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.v3.oas.models.media.Schema;

public class ObjectGenerator extends DataGenerator {
	private static final int MAP_NUM_ITEMS_GENERATED = 2;
	
	private IDataGeneratorFactory factory;

	public ObjectGenerator(String name, Schema<?> schema, IDataGeneratorFactory factory) {
		super(name, schema);
		this.factory = factory;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public JsonNode generateJsonValue() {
		ObjectNode allObjects = new ObjectMapper().createObjectNode();
		// Can have the standard properties or additional properties (map)
		if (schema.getProperties() != null) {
			for (Entry<String, Schema> prop : schema.getProperties().entrySet()) {
				DataGenerator item = factory.getItem(prop.getKey(), prop.getValue());
				JsonNode node = item.generateJsonValue();
				allObjects.set(item.name, node);
			}
		} else if (schema.getAdditionalProperties() != null) {
			// Each additional property has a string key and a value according to the given schema
			Schema<?> valueSchema = (Schema<?>) schema.getAdditionalProperties();
			for (int i = 0; i < MAP_NUM_ITEMS_GENERATED; i++) {
				String key = this.name + "Key" + i; // could be random
				JsonNode value = factory.getItem(key, valueSchema).generateJsonValue();
				allObjects.set(key, value);
			}
		}
		
		return allObjects;
	}
}
