package com.zerocode.openapi.template.processor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.commons.lang.math.NumberUtils;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.domain.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.oas.inflector.examples.ExampleBuilder;
import io.swagger.oas.inflector.examples.models.Example;
import io.swagger.oas.inflector.processors.JsonNodeExampleSerializer;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.parser.OpenAPIV3Parser;
import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * The Class JsonTransformer.
 */
@Component

@Slf4j
public class OpenAPIStepsGenerator {
	static {
		// register the JSON serializer
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(new JsonNodeExampleSerializer());
		Json.mapper().registerModule(simpleModule);
		Yaml.mapper().registerModule(simpleModule);
	}

	/** The source. */
	@Value("${source}")
	String source;

	/** The target. */
	@Value("${target}")
	String target;

	/** The target. */
	@Value("${template.dir}")
	String templateDir;

	/** The template location. */
	@Autowired
	Map<String, String> templateLocation;

	/** The obj mapper. */
	ObjectMapper objMapper = new ObjectMapper();

	/**
	 * Transform.
	 */
	public void transform() {
		OpenAPI openAPI = (new OpenAPIV3Parser()).read(source, null, null);
		List<Step> steps = populateSteps(openAPI);
		ScenarioSpec scenario = new ScenarioSpec(null, false, openAPI.getOpenapi(), steps);
		ouputStepsToFile(scenario, source, target);
	}

	/**
	 * Ouput steps to file.
	 *
	 * @param steps  the steps
	 * @param source the source
	 * @param target the target
	 */
	private void ouputStepsToFile(ScenarioSpec scenario, String source, String target) {
		File file = null;
		try {
			file = new File(target);
			if (file.exists()) {
				log.error("File {} already exists  ", target);
				throw new IllegalArgumentException("File already exist");
			}
			file.createNewFile();
			objMapper.enable(SerializationFeature.INDENT_OUTPUT);
			objMapper.writerWithDefaultPrettyPrinter().writeValue(file, scenario);
		} catch (Exception e) {
			log.error("Error writing output {} ", target, e);
		} finally {
		}
	}

	/**
	 * Populate steps.
	 *
	 * @param openAPI       the open API
	 * @param schemaObjects the schema objects
	 * @return the list
	 */
	private List<Step> populateSteps(OpenAPI openAPI) {
		String baseURL = openAPI.getServers().get(0).getUrl();
		List<Step> steps = new LinkedList<>();
		openAPI.getPaths().keySet().forEach(p -> {
			PathItem path = openAPI.getPaths().get(p);
			path.readOperationsMap().keySet().stream().forEach(o -> {
				Operation operation = path.readOperationsMap().get(o);
				String name = operation.getOperationId();
				String url = null;
				try {
					url = prepareURL(operation, openAPI, baseURL + p);
				} catch (URISyntaxException e) {
					// Do nothing
				}
				JsonNode request = prepareRequest(operation, openAPI);
				JsonNode assertions = prepareAssertion(operation, openAPI);
				steps.add(new Step(null, name, o.toString(), url, request, assertions));
			});
		});
		return steps;
	}

	/**
	 * Prepare addsertion.
	 *
	 * @param operation     the operation
	 * @param openAPI       the open API
	 * @param schemaObjects the schema objects
	 * @return the json node
	 */
	private JsonNode prepareAssertion(Operation operation, OpenAPI openAPI) {
		ObjectNode objectNode = objMapper.createObjectNode();
		operation.getResponses().keySet().forEach(r -> {
			if (NumberUtils.isNumber((String) r)) {
				objectNode.set("status", objMapper.getNodeFactory().numberNode(Integer.valueOf((String) r)));
				ApiResponse response = operation.getResponses().get(r);
				if (response != null && response.getContent() != null && !response.getContent().values().isEmpty()) {
					objectNode.set("body",
							getSchemaType(openAPI, response.getContent().values().iterator().next().getSchema()));
				}
			}
		});
		return objectNode;
	}

	/**
	 * Prepare request.
	 *
	 * @param operation     the operation
	 * @param openAPI       the open API
	 * @param schemaObjects the schema objects
	 * @return the json node
	 */
	private JsonNode prepareRequest(Operation operation, OpenAPI openAPI) {
		ObjectNode objectNode = objMapper.createObjectNode();
		populateRequestHeader(objectNode, operation, openAPI);
		populateRequestBody(objectNode, operation, openAPI);
		return objectNode;
	}

	/**
	 * Populate request body.
	 *
	 * @param objectNode    the object node
	 * @param operation     the operation
	 * @param openAPI       the open API
	 * @param schemaObjects the schema objects
	 */
	private void populateRequestBody(ObjectNode objectNode, Operation operation, OpenAPI openAPI) {
		if (operation.getRequestBody() != null) {
			RequestBody body = operation.getRequestBody();
			if (body.getContent() != null && !body.getContent().values().isEmpty()) {
				objectNode.set("body",
						getSchemaType(openAPI, body.getContent().values().iterator().next().getSchema()));

			}
		}

	}

	/**
	 * Populate request header.
	 *
	 * @param objectNode    the object node
	 * @param operation     the operation
	 * @param openAPI       the open API
	 * @param schemaObjects the schema objects
	 */
	private void populateRequestHeader(ObjectNode objectNode, Operation operation, OpenAPI openAPI) {
		// TODO Auto-generated method stub

	}

	/**
	 * Prepare URL.
	 *
	 * @param operation the operation
	 * @param openAPI   the open API
	 * @param baseURL   the base URL
	 * @return the string
	 * @throws URISyntaxException the URI syntax exception
	 */
	private String prepareURL(Operation operation, OpenAPI openAPI, String baseURL) throws URISyntaxException {
		StringBuilder builder = new StringBuilder(baseURL);
		StringJoiner paramJoiner = new StringJoiner("=");
	   StringBuilder firstSeperator = new StringBuilder("");
		if (operation.getParameters() != null) {
			operation.getParameters().stream().forEach(p -> {
				if ("query".equalsIgnoreCase(p.getIn())) {
					paramJoiner.add(firstSeperator.toString() + p.getName());
					if(firstSeperator.toString().trim().equals("")) {
						firstSeperator.append("&");
					}
				}

			});
		}
		return builder.append(paramJoiner.length() > 0 ? "?" + paramJoiner.toString() : "").toString();
	}

	/**
	 * Populate schema type.
	 *
	 * @param components the components
	 * @param schema     the schema
	 * @param key        the key
	 * @return the schema type
	 */
	private JsonNode getSchemaType(OpenAPI openAPI, Schema schema) {
		ObjectMapper mapper = new ObjectMapper();
		Example rep = ExampleBuilder.fromSchema(schema, openAPI.getComponents().getSchemas());
		try {
			return mapper.readTree(Json.pretty(rep));
		} catch (IOException e) {
			log.error("Error creating example json",e);
		}
		return null;
	}

}
