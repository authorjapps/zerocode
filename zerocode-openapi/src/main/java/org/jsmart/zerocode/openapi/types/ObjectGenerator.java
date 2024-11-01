package org.jsmart.zerocode.openapi.types;

import java.util.Map.Entry;

import org.jsmart.zerocode.openapi.IDataGeneratorFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.v3.oas.models.media.Schema;

public class ObjectGenerator extends DataGenerator {
	private IDataGeneratorFactory factory;

	public ObjectGenerator(String name, Schema<?> schema, IDataGeneratorFactory factory) {
		super(name, schema);
		this.factory = factory;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public JsonNode generateJsonValue() {
		ObjectNode allObjects = new ObjectMapper().createObjectNode();
		for (Entry<String, Schema> prop : schema.getProperties().entrySet()) {
			DataGenerator item = factory.getItem(prop.getKey(), prop.getValue());
			JsonNode node = item.generateJsonValue();
			allObjects.set(item.name, node);
		}
		
		return allObjects;
	}
}
