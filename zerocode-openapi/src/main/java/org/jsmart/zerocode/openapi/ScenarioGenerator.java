package org.jsmart.zerocode.openapi;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.domain.Step;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;

public class ScenarioGenerator {

    private static final org.slf4j.Logger LOGGER = getLogger(ScenarioGenerator.class);

    /**
     * Generates Zerocode scenarios from an OpenAPI specification.
     * The name of the resulting scenarios is the name of the path
     * (other characters different from alphanumeric, brackets, dots and dashes are replaced by _).
     * Each scenario contains a step for each operation defined in the specification.
     * 
     * @param oaSpecLocation Yaml or Json file or url with the OpenAPI specification.
     * @param outFolder Folder where the scenarios are generated.
     */
    public void generateAll(String oaSpecLocation, String outFolder) {
		OpenAPI openApi = parseOpenApi(oaSpecLocation);
		Map<String, PathItem> oaPaths = openApi.getPaths();
		for (Entry<String, PathItem> oaPath : oaPaths.entrySet()) {
			LOGGER.info("Generating scenario for path: {}", oaPath.getKey());
			generateScenario(oaPath.getKey(), oaPath.getValue(), outFolder);
		}
	}

	private void generateScenario(String path, PathItem oaPathValue, String outFolder) {
		List<Step> steps = new ArrayList<>();
		generateStep(path, "POST", oaPathValue.getPost(), steps);
		generateStep(path, "GET", oaPathValue.getGet(), steps);
		generateStep(path, "PUT", oaPathValue.getPut(), steps);
		generateStep(path, "PATCH", oaPathValue.getPatch(), steps);
		generateStep(path, "HEAD", oaPathValue.getHead(), steps);
		generateStep(path, "OPTIONS", oaPathValue.getOptions(), steps);
		generateStep(path, "TRACE", oaPathValue.getTrace(), steps);
		generateStep(path, "DELETE", oaPathValue.getDelete(), steps);

		ScenarioSpec scenario = new ScenarioSpec(null, false, path + " - Test scenario", steps, null, null);
		writeScenario(scenario, outFolder, path);
	}

	private OpenAPI parseOpenApi(String location) {
		LOGGER.info("Parsing OpenAPI specification file: {}", location);
		ParseOptions parseOptions = new ParseOptions();
		parseOptions.setResolve(true);
		parseOptions.setResolveFully(true); // for refs
		return new OpenAPIV3Parser().read(location, null, parseOptions);
	}
	
	private void generateStep(String path, String method, Operation oaOperation, List<Step> outSteps) {
		if (oaOperation == null)
			return;
		try {
			StepGenerator generator=new StepGenerator();
			Step step = generator.parseOperation(path, method, oaOperation);
			outSteps.add(step);
		} catch (RuntimeException e) {
			// Prevents the whole scenario failure if some step fails (error, not supported feature, etc.)
			LOGGER.error("Failed step generation for {} {}", method, path, e);
		}
	}

	private void writeScenario(ScenarioSpec scenario, String outFolder, String name) {
		try {
			String scenarioStr = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(scenario);
			String fileName = name.replaceAll("[^A-Za-z0-9 \\{\\}\\-]", "_") + ".json";
			LOGGER.info("Writing scenario to file: {}", fileName);
			FileUtils.writeStringToFile(new File(FilenameUtils.concat(outFolder, fileName)), scenarioStr, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
