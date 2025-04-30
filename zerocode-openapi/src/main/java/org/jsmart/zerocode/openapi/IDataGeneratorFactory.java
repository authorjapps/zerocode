package org.jsmart.zerocode.openapi;

import org.jsmart.zerocode.openapi.types.DataGenerator;

import io.swagger.v3.oas.models.media.Schema;

public interface IDataGeneratorFactory {
	
	DataGenerator getItem(String name, Schema<?> schema);
	
}