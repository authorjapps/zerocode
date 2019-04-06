package com.zerocode.openapi.template.processor;

import java.io.File;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.domain.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.PathItem.HttpMethod;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
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

/** The Constant log. */

/** The Constant log. */

/** The Constant log. */

/** The Constant log. */

/** The Constant log. */

/** The Constant log. */

/** The Constant log. */

/** The Constant log. */

/** The Constant log. */
@Slf4j
public class JsonTransformer {

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
		OpenAPI openAPI = (new OpenAPIV3Parser()).read(source);
		// Get All Schema types
		Map<String, Object> schemaObjects = populateSchemaTypes(openAPI);
		List<Step> steps = populateSteps(openAPI, schemaObjects);
		ScenarioSpec scenario= new ScenarioSpec(null, false, openAPI.getOpenapi(), steps);
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
		// try-with-resources statement based on post comment below :)
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
	private List<Step> populateSteps(OpenAPI openAPI, Map<String, Object> schemaObjects) {
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
				JsonNode request = prepareRequest(operation, openAPI, schemaObjects);
				JsonNode assertions = prepareAssertion(operation, openAPI, schemaObjects);
				steps.add(new Step(null, name, o.toString(), url, request, assertions));
				// processPath(path,schemaObject)
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
	private JsonNode prepareAssertion(Operation operation, OpenAPI openAPI, Map<String, Object> schemaObjects) {
		ObjectNode objectNode = objMapper.createObjectNode();
		operation.getResponses().keySet().forEach(r -> {
			if (NumberUtils.isNumber((String) r)) {
				objectNode.set("status", objMapper.getNodeFactory().numberNode(Integer.valueOf((String) r)));
				ApiResponse response = operation.getResponses().get(r);
				if (response != null && !response.getContent().values().isEmpty()) {
					objectNode.set("body", getSchemaType(openAPI.getComponents(),
							response.getContent().values().iterator().next().getSchema(), null));
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
	private JsonNode prepareRequest(Operation operation, OpenAPI openAPI, Map<String, Object> schemaObjects) {
		ObjectNode objectNode = objMapper.createObjectNode();
		populateRequestHeader(objectNode, operation, openAPI, schemaObjects);
		populateRequestBody(objectNode, operation, openAPI, schemaObjects);
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
	private void populateRequestBody(ObjectNode objectNode, Operation operation, OpenAPI openAPI,
			Map<String, Object> schemaObjects) {
		if (operation.getRequestBody() != null) {
			RequestBody body = operation.getRequestBody();
			if (body.getContent() != null && !body.getContent().values().isEmpty()) {
				objectNode.setAll(getSchemaType(openAPI.getComponents(),
						body.getContent().values().iterator().next().getSchema(), "body"));

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
	private void populateRequestHeader(ObjectNode objectNode, Operation operation, OpenAPI openAPI,
			Map<String, Object> schemaObjects) {
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
		StringJoiner paramJoiner = new StringJoiner("=", "&", "");
		if (operation.getParameters() != null) {
			operation.getParameters().stream().forEach(p -> {
				if ("query".equalsIgnoreCase(p.getIn())) {
					paramJoiner.add(p.getName());
				}
			});
		}
		return builder.append(paramJoiner.length() > 0 ? "?" + paramJoiner.toString() : "").toString();
	}

	/**
	 * Populate schema types.
	 *
	 * @param openAPI the open API
	 * @return the map
	 */
	private Map<String, Object> populateSchemaTypes(OpenAPI openAPI) {
		Map<String, Object> schemaMap = new LinkedHashMap<>();
		if (openAPI != null && openAPI.getComponents() != null && openAPI.getComponents().getSchemas() != null) {
			openAPI.getComponents().getSchemas().keySet().forEach(p -> {
				log.debug("Populating schematype {} ", p);
				ObjectNode schemaNode = getSchemaType(openAPI.getComponents(),
						openAPI.getComponents().getSchemas().get(p), null);
				log.debug("Caching object{} against schematype {} ", p, schemaNode);
				schemaMap.put(p, schemaNode);
			});
		} else {
			log.debug("No components found");
		}
		return schemaMap;
	}

	/**
	 * Populate schema type.
	 *
	 * @param components the components
	 * @param schema     the schema
	 * @param key        the key
	 * @return the schema type
	 */
	// TODO: Handle it well this method is doing too much and is way too complicated
	private ObjectNode getSchemaType(Components components, Schema schema, String key) {
		log.debug("populating schema {} ", schema);
		ObjectNode schemaNode = objMapper.createObjectNode();
		if (schema instanceof ComposedSchema) {
			log.debug("Composed schema  {} ", schema);
			ComposedSchema composedSchema = (ComposedSchema) schema;
			composedSchema.getAllOf().stream().forEach(s -> schemaNode.setAll(getSchemaType(components, s, null)));
		} else if (!StringUtils.isEmpty(schema.get$ref())) {
			log.debug("RefType  {} ", schema.get$ref());
			schemaNode.setAll(getSchemaType(components,
					components.getSchemas().get(schema.get$ref().substring(schema.get$ref().lastIndexOf("/") + 1)),
					null));
		} else if ("Array".equalsIgnoreCase(schema.getType())) {
			ArraySchema arrSchema = (ArraySchema) schema;
			ArrayNode arrNode = schemaNode.arrayNode();
			arrNode.add(getSchemaType(components, arrSchema.getItems(), null));
			schemaNode.set(key == null ? "" : key, arrNode);
		} else {
			log.debug("Object Schema {}", schema);
			// TODO more work needed to handle object and array nested types
			if (schema.getProperties() != null) {
				schema.getProperties().keySet().stream().forEach(k -> {
					Schema tempSchema = (Schema) schema.getProperties().get(k);
					String type = tempSchema.getType();
					if ("Array".equalsIgnoreCase(type)) {
						schemaNode.setAll(getSchemaType(components, tempSchema, (String) k));
					} else if ("object".equalsIgnoreCase(type)) {
						schemaNode.set((String) k, getSchemaType(components, tempSchema, (String) k));
					} else {
						schemaNode.set((String) k, schemaNode.textNode(""));
					}
				});
			}

		}
		return schemaNode;

	}

}
