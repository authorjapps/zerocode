package org.jsmart.zerocode.openapi;

import org.jsmart.zerocode.openapi.types.DataGenerator;
import org.jsmart.zerocode.openapi.types.ArrayGenerator;
import org.jsmart.zerocode.openapi.types.BooleanGenerator;
import org.jsmart.zerocode.openapi.types.IntegerGenerator;
import org.jsmart.zerocode.openapi.types.NumberGenerator;
import org.jsmart.zerocode.openapi.types.ObjectGenerator;
import org.jsmart.zerocode.openapi.types.StringGenerator;

import io.swagger.v3.oas.models.media.Schema;

public class DataGeneratorFactory implements IDataGeneratorFactory {

	public DataGenerator getItem(String name, Schema<?> schema) {
		if ("integer".equals(schema.getType())) {
			return new IntegerGenerator(name, schema);
		} else if ("number".equals(schema.getType())) {
			return new NumberGenerator(name, schema);
		} else if ("string".equals(schema.getType())) {
			return new StringGenerator(name, schema);
		} else if ("boolean".equals(schema.getType())) {
			return new BooleanGenerator(name, schema);
		} else if ("array".equals(schema.getType())) {
			return new ArrayGenerator(name, schema, this); // requires factory to create objects
		} else if ("object".equals(schema.getType())) {
			return new ObjectGenerator(name, schema, this);
		}
		throw new RuntimeException(
				String.format("OpenAPI schema type %s not allowed, property: %s", schema.getType(), name));
	}
}
