package org.jsmart.zerocode.openapi.types;

import org.jsmart.zerocode.openapi.IDataGeneratorFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.swagger.v3.oas.models.media.Schema;

public class ArrayGenerator extends DataGenerator {
	private static final int ARRAY_NUM_ITEMS_GENERATED = 2;
	
	private IDataGeneratorFactory factory;
	private Schema<?> items;

	public ArrayGenerator(String name, Schema<?> schema, IDataGeneratorFactory factory) {
		super(name, schema);
		this.factory = factory;
		items = schema.getItems();
	}

	@Override
	public JsonNode generateJsonValue() {
		ArrayNode allObjects = new ObjectMapper().createArrayNode();
		for (int i = 0; i < ARRAY_NUM_ITEMS_GENERATED; i++) { //propagates required encoding to the array values
			DataGenerator item = factory.getItem(name, items).setRequireUrlEncode(this.requireUrlEncode);
			allObjects.add(item.generateJsonValue());
		}
		return allObjects;
	}
}
