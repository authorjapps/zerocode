package org.jsmart.zerocode.openapi;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jsmart.zerocode.openapi.types.DataGenerator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.v3.oas.models.parameters.Parameter;

/**
 * OpenAPI supports parameters at several places
 *   https://swagger.io/docs/specification/v3_0/describing-parameters/
 * Currently: query string, path and header parameters are supported (no cookie params)
 * 
 * There are multiple formats for manage non primitive values:
 * https://swagger.io/docs/specification/v3_0/serialization/
 * Currently: the supported parameters are
 * - path: primitive
 * - query string: primitive, array (Styles delimited by comma, space and pipe, others ignored)
 * - headers: primitive
 * 
 * Error handling: Warn and ignore if a data type or style is not supported
 */
public class ParameterSerializer {

    private static final org.slf4j.Logger LOGGER = getLogger(ParameterSerializer.class);

	public String serializePathParams(String path, List<Parameter> oaParams) {
		for (Parameter oaParam : filterParams(oaParams, "path")) {
			if (!isObject(oaParam) && !isArray(oaParam)) {
				DataGeneratorFactory factory = new DataGeneratorFactory();
				JsonNode value = factory.getItem(oaParam.getName(), oaParam.getSchema()).setRequireUrlEncode(true).generateJsonValue();
				path = path.replace("{" + oaParam.getName() + "}", value.asText());
			} else {
				LOGGER.warn("Non primitive path parameters are not supported yet");
			}
		}
		return path;
	}

	public JsonNode getQueryParams(List<Parameter> oaParams) {
		ObjectNode params = new ObjectMapper().createObjectNode();
		for (Parameter oaParam : filterParams(oaParams, "query")) {
			// Only primitive params are in the header, other types are set in the path
			if (!isObject(oaParam) && !isArray(oaParam)) {
				DataGeneratorFactory factory = new DataGeneratorFactory();
				JsonNode value = factory.getItem(oaParam.getName(), oaParam.getSchema()).generateJsonValue();
				params.set(oaParam.getName(), value);
			}
		}
		return params;
	}

	public String serializeQueryParams(String path, List<Parameter> oaParams) {
		List<String> queryString = new ArrayList<>();
		for (Parameter oaParam : filterParams(oaParams, "query")) {
			if (isArray(oaParam)) {
				String params = getArrayQueryParams(oaParam);
				if (params != null)
					queryString.add(params);
			} else if (isObject(oaParam)) {
				LOGGER.warn("Object query parameters are not supported yet");
			} // primitive are not serialized in the path
		}
		// For now, generation is quite simple, not using any url template library
		// Assuming the OpenAPI spec has no query strings in the url
		if (!queryString.isEmpty())
			path = path + "?" + String.join("&", queryString);
		return path;
	}

	private String getArrayQueryParams(Parameter oaParam) {
		DataGeneratorFactory factory = new DataGeneratorFactory();
		DataGenerator generator = factory.getItem(oaParam.getName(), oaParam.getSchema()).setRequireUrlEncode(true);
		String paramName = generator.encodeIfRequired(oaParam.getName()); // values will be encoded when generated
		String arraySeparator = getArraySeparator(oaParam.getStyle());
		if (arraySeparator == null) {
			LOGGER.warn("Array query parameter style {} is not supported yet", oaParam.getStyle().toString());
			return null; // to not add any parameter
		}
		
		JsonNode jsonArray = generator.generateJsonValue();
		List<String> items = new ArrayList<>();
		for (JsonNode value : jsonArray) {
			if (oaParam.getExplode())
				items.add(paramName + "=" + value.textValue());
			else
				items.add(value.textValue());
		}
		if (oaParam.getExplode())
			return String.join("&", items);
		else
			return paramName + "=" + String.join(arraySeparator, items);
	}

	private String getArraySeparator(Parameter.StyleEnum style) {
		if (style == Parameter.StyleEnum.FORM)
			return ",";
		else if (style == Parameter.StyleEnum.SPACEDELIMITED)
			return "%20";
		else if (style == Parameter.StyleEnum.PIPEDELIMITED)
			return "|";
		else
			return null;
	}

	public JsonNode getHeaderParams(List<Parameter> oaParams) {
		ObjectNode params = new ObjectMapper().createObjectNode();
		for (Parameter oaParam : filterParams(oaParams, "header")) {
			// Specification has only the "simple" style, no considering explode
			// If considering explode eventually, refactor with getArrayQueryParams
			DataGeneratorFactory factory = new DataGeneratorFactory();
			JsonNode value = factory.getItem(oaParam.getName(), oaParam.getSchema()).generateJsonValue();
			params.set(oaParam.getName(), value);
		}
		return params;
	}

	private List<Parameter> filterParams(List<Parameter> oaParams, String in) {
		if (oaParams == null)
			return new ArrayList<>();
		return oaParams.stream().filter(oaParam -> in.equals(oaParam.getIn())).collect(Collectors.toList());
	}

	private boolean isObject(Parameter oaParam) {
		return "object".equals(oaParam.getSchema().getType());
	}
	
	private boolean isArray(Parameter oaParam) {
		return "array".equals(oaParam.getSchema().getType());
	}

}
