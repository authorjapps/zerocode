package com.zerocode.oas.stepgen.shell;

import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.zerocode.oas.stepgen.generator.OpenAPIStepsGenerator;
import com.zerocode.oas.stepgen.io.IOUtils;

import io.swagger.v3.oas.models.OpenAPI;
import lombok.extern.slf4j.Slf4j;

@ShellComponent
@Slf4j
public class StepgenShell {

	@Autowired
	IOUtils ioUtils;

	@Autowired
	OpenAPIStepsGenerator stepsGenerator;

	@ShellMethod(value = "Generate Zerocode scenarios from openapi spec url/file", group = "Stepgen Commands", key = "zc-step-gen")
	public String zerocodeStepGeneration(String source, String target, boolean o) {
		OpenAPI openAPI = ioUtils.readOpenAPISpecFromLocation(source);
		log.info("OpenAPI parsed successfully from location {}", source);
		ScenarioSpec scenarioSpec = stepsGenerator.transform(openAPI);
		log.info("Transformed openAPI spec into zerocode steps successfully!!");
		ioUtils.ouputStepsToFile(scenarioSpec, target, o);
		return "Steps generated successfully!!!!";
	}
}
