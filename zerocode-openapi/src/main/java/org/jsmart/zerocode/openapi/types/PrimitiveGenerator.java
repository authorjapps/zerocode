package org.jsmart.zerocode.openapi.types;

import java.util.List;

import io.swagger.v3.oas.models.media.Schema;

public abstract class PrimitiveGenerator extends DataGenerator {

	public PrimitiveGenerator(String name, Schema<?> schema) {
		super(name, schema);
	}

	protected boolean hasEnum() {
		return schema.getEnum() != null;
	}

	protected Object getEnumItem() {
		return getOneOf(schema.getEnum());
		// There is no token like "$ONE.OF:[San Francisco, New York, Seattle]", only for asserts
	}

	protected Object getOneOf(List<?> values) {
		int position = new RandomGeneratorWithSeed().nextInt(0, values.size() - 1);
		return values.get(position);
	}
	
	protected String getFormat() {
		return schema.getFormat();
	}
}
