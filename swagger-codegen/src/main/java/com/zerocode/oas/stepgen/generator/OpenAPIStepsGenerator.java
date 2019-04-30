package com.zerocode.oas.stepgen.generator;

import org.jsmart.zerocode.core.domain.ScenarioSpec;

import io.swagger.v3.oas.models.OpenAPI;

public interface OpenAPIStepsGenerator {

	/**
	 * Transform.
	 */
	ScenarioSpec transform(OpenAPI source);

}